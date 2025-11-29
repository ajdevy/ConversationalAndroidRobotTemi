package com.temi.conversationalrobot.data.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.domain.services.SpeakingState
import com.temi.conversationalrobot.domain.services.TtsEvent
import com.temi.conversationalrobot.domain.services.TtsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class TtsServiceImpl(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) : TtsService {
    
    private val _speakingState = MutableStateFlow<SpeakingState>(SpeakingState.Idle)
    override val speakingState: StateFlow<SpeakingState> = _speakingState.asStateFlow()
    
    private val _ttsEvents = MutableSharedFlow<TtsEvent>(replay = 0)
    override val ttsEvents: Flow<TtsEvent> = _ttsEvents.asSharedFlow()
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    
    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    _ttsEvents.tryEmit(TtsEvent.Error("Language not supported"))
                } else {
                    isInitialized = true
                }
            } else {
                _ttsEvents.tryEmit(TtsEvent.Error("TTS initialization failed"))
            }
        }
        
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _speakingState.value = SpeakingState.Speaking
                _ttsEvents.tryEmit(TtsEvent.Started)
            }
            
            override fun onDone(utteranceId: String?) {
                _speakingState.value = SpeakingState.Idle
                _ttsEvents.tryEmit(TtsEvent.Completed)
            }
            
            override fun onError(utteranceId: String?) {
                _speakingState.value = SpeakingState.Idle
                _ttsEvents.tryEmit(TtsEvent.Error("TTS error"))
            }
        })
        
        // Subscribe to speech rate changes
        serviceScope.launch {
            settingsRepository.getSettings().collect { settings ->
                updateSpeechRate(settings.ttsSpeechRate)
            }
        }
    }
    
    override fun speak(text: String) {
        serviceScope.launch {
            if (!isInitialized) {
                _ttsEvents.emit(TtsEvent.FallbackToText(text))
                return@launch
            }
            
            val settings = settingsRepository.getSettings().first()
            updateSpeechRate(settings.ttsSpeechRate)
            
            val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utterance_id")
            if (result == TextToSpeech.ERROR) {
                _ttsEvents.emit(TtsEvent.FallbackToText(text))
            }
        }
    }
    
    override fun stop() {
        tts?.stop()
        _speakingState.value = SpeakingState.Idle
    }
    
    private fun updateSpeechRate(rateWpm: Float) {
        // Map WPM (50-250) to TTS speed (0.5-2.5)
        val speed = (rateWpm / 100f).coerceIn(0.5f, 2.5f)
        tts?.setSpeechRate(speed)
    }
    
    fun cleanup() {
        stop()
        tts?.shutdown()
        tts = null
        serviceScope.cancel()
    }
}

