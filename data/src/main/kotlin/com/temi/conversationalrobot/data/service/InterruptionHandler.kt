package com.temi.conversationalrobot.data.service

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.domain.services.InterruptionEvent
import com.temi.conversationalrobot.domain.services.InterruptionHandler
import com.temi.conversationalrobot.domain.services.SpeakingState
import com.temi.conversationalrobot.domain.services.TtsService
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

class InterruptionHandlerImpl(
    private val ttsService: TtsService,
    private val settingsRepository: SettingsRepository
) : InterruptionHandler {
    
    private val _interruptionEvents = MutableSharedFlow<InterruptionEvent>(replay = 0)
    override val interruptionEvents: Flow<InterruptionEvent> = _interruptionEvents.asSharedFlow()
    
    private val handlerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isMonitoring = false
    private var audioRecord: AudioRecord? = null
    
    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        private const val SPEECH_THRESHOLD = 2000 // Amplitude threshold for speech detection
    }
    
    init {
        handlerScope.launch {
            ttsService.speakingState.collect { state ->
                val settings = settingsRepository.getSettings().first()
                if (state == SpeakingState.Speaking && settings.interruptionDetectionEnabled) {
                    startMonitoring()
                } else {
                    stopMonitoring()
                }
            }
        }
    }
    
    override fun startMonitoring() {
        if (isMonitoring) return
        
        handlerScope.launch {
            try {
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    BUFFER_SIZE * 2
                )
                
                audioRecord?.startRecording()
                isMonitoring = true
                
                monitorAudio()
            } catch (e: Exception) {
                stopMonitoring()
            }
        }
    }
    
    override fun stopMonitoring() {
        isMonitoring = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
    
    private suspend fun monitorAudio() {
        val buffer = ShortArray(BUFFER_SIZE)
        
        while (coroutineContext.isActive && isMonitoring) {
            val readResult = audioRecord?.read(buffer, 0, BUFFER_SIZE) ?: 0
            
            if (readResult > 0) {
                var sum = 0L
                for (i in 0 until readResult) {
                    sum += kotlin.math.abs(buffer[i].toLong())
                }
                val averageAmplitude = sum / readResult
                
                if (averageAmplitude > SPEECH_THRESHOLD) {
                    _interruptionEvents.emit(InterruptionEvent.UserSpeechDetected)
                    stopMonitoring()
                }
            }
        }
    }
    
    fun cleanup() {
        stopMonitoring()
        handlerScope.cancel()
    }
}

