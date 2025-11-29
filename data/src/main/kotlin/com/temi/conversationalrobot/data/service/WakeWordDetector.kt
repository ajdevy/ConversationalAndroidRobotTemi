package com.temi.conversationalrobot.data.service

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

interface WakeWordDetector {
    val detectionEvents: Flow<WakeWordDetectionEvent>
    fun startListening()
    fun stopListening()
    fun isListening(): Boolean
}

sealed class WakeWordDetectionEvent {
    object Detected : WakeWordDetectionEvent()
    data class Error(val message: String) : WakeWordDetectionEvent()
}

class WakeWordDetectorImpl(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : WakeWordDetector {
    
    private val _detectionEvents = MutableSharedFlow<WakeWordDetectionEvent>(replay = 0)
    override val detectionEvents: Flow<WakeWordDetectionEvent> = _detectionEvents.asSharedFlow()
    
    private val detectorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isListening = false
    private var audioRecord: AudioRecord? = null
    
    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        private const val WAKE_WORD = "Hey, Temi"
        
        // Sensitivity thresholds (amplitude-based detection)
        private val SENSITIVITY_THRESHOLDS = mapOf(
            0 to 5000,  // Low sensitivity
            1 to 3000,  // Medium sensitivity
            2 to 1500   // High sensitivity
        )
    }
    
    init {
        // Subscribe to settings changes
        detectorScope.launch {
            settingsRepository.getSettings().collect { settings ->
                if (!settings.wakeWordEnabled && isListening) {
                    stopListening()
                } else if (settings.wakeWordEnabled && !isListening) {
                    startListening()
                }
            }
        }
    }
    
    override fun startListening() {
        if (isListening) return
        
        detectorScope.launch {
            val settings = settingsRepository.getSettings().first()
            if (!settings.wakeWordEnabled) return@launch
            
            try {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    BUFFER_SIZE * 2
                ).also { recorder ->
                    if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                        recorder.release()
                        throw IllegalStateException("AudioRecord failed to initialize")
                    }
                }
                
                audioRecord?.startRecording()
                isListening = true
                
                processAudio(settings.wakeWordSensitivity)
            } catch (e: Exception) {
                _detectionEvents.emit(WakeWordDetectionEvent.Error("Failed to start listening: ${e.message}"))
                stopListening()
            }
        }
    }
    
    override fun stopListening() {
        isListening = false
        audioRecord?.let { recorder ->
            val isInitialized = recorder.state == AudioRecord.STATE_INITIALIZED
            val isRecording = recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING
            if (isInitialized && isRecording) {
                try {
                    recorder.stop()
                } catch (ignored: IllegalStateException) {
                    // Ignore illegal state from stop to keep shutdown safe
                }
            }
            if (isInitialized) {
                recorder.release()
            } else {
                runCatching { recorder.release() }
            }
        }
        audioRecord = null
    }
    
    override fun isListening(): Boolean = isListening
    
    private suspend fun processAudio(sensitivity: Int) {
        val buffer = ShortArray(BUFFER_SIZE)
        val threshold = SENSITIVITY_THRESHOLDS[sensitivity.coerceIn(0, 2)] ?: 3000
        var consecutiveHighSamples = 0
        val requiredConsecutiveSamples = 10
        
        while (coroutineContext.isActive && isListening) {
            val readResult = audioRecord?.read(buffer, 0, BUFFER_SIZE) ?: 0
            
            if (readResult > 0) {
                // Calculate average amplitude
                var sum = 0L
                for (i in 0 until readResult) {
                    sum += kotlin.math.abs(buffer[i].toLong())
                }
                val averageAmplitude = sum / readResult
                
                // Simple voice activity detection
                if (averageAmplitude > threshold) {
                    consecutiveHighSamples++
                    if (consecutiveHighSamples >= requiredConsecutiveSamples) {
                        // Potential wake word detected
                        _detectionEvents.emit(WakeWordDetectionEvent.Detected)
                        consecutiveHighSamples = 0
                        // Brief pause to avoid multiple detections
                        kotlinx.coroutines.delay(500)
                    }
                } else {
                    consecutiveHighSamples = 0
                }
            }
        }
    }
    
    fun cleanup() {
        stopListening()
        detectorScope.cancel()
    }
}

