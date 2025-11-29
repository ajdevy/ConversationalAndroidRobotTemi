package com.temi.conversationalrobot.data.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import com.temi.conversationalrobot.domain.services.SttResult
import com.temi.conversationalrobot.domain.services.SttService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale

class SttServiceImpl(
    private val context: Context
) : SttService, RecognitionListener {
    
    private val _transcriptionResults = MutableSharedFlow<SttResult>(replay = 0)
    override val transcriptionResults: Flow<SttResult> = _transcriptionResults.asSharedFlow()
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var timeoutJob: kotlinx.coroutines.Job? = null
    
    companion object {
        private const val TIMEOUT_MS = 5000L // 5 seconds
    }
    
    override fun startListening() {
        if (isListening) return
        
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _transcriptionResults.tryEmit(SttResult.Error("Speech recognition not available"))
            return
        }
        
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(this)
            
            val intent = Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString())
                putExtra(android.speech.RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            
            speechRecognizer?.startListening(intent)
            isListening = true
            
            // Start timeout
            timeoutJob = serviceScope.launch {
                delay(TIMEOUT_MS)
                if (isListening) {
                    _transcriptionResults.emit(SttResult.Timeout)
                    stopListening()
                }
            }
        } catch (e: Exception) {
            _transcriptionResults.tryEmit(SttResult.Error("Failed to start listening: ${e.message}"))
        }
    }
    
    override fun stopListening() {
        isListening = false
        timeoutJob?.cancel()
        timeoutJob = null
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
    }
    
    override fun isListening(): Boolean = isListening
    
    override fun onReadyForSpeech(params: Bundle?) {}
    
    override fun onBeginningOfSpeech() {}
    
    override fun onRmsChanged(rmsdB: Float) {}
    
    override fun onBufferReceived(buffer: ByteArray?) {}
    
    override fun onEndOfSpeech() {
        stopListening()
    }
    
    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech match found"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_AUDIO -> "Audio error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            else -> "Unknown error: $error"
        }
        _transcriptionResults.tryEmit(SttResult.Error(errorMessage))
        stopListening()
    }
    
    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull() ?: ""
        if (text.isNotEmpty()) {
            _transcriptionResults.tryEmit(SttResult.Success(text))
        } else {
            _transcriptionResults.tryEmit(SttResult.Error("No transcription result"))
        }
        stopListening()
    }
    
    override fun onPartialResults(partialResults: Bundle?) {
        // Handle partial results if needed
    }
    
    override fun onEvent(eventType: Int, params: Bundle?) {}
    
    fun cleanup() {
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        serviceScope.cancel()
    }
}

