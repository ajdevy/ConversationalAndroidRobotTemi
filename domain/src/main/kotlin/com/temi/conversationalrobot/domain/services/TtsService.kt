package com.temi.conversationalrobot.domain.services

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TtsService {
    val speakingState: StateFlow<SpeakingState>
    val ttsEvents: Flow<TtsEvent>
    fun speak(text: String)
    fun stop()
}

sealed class SpeakingState {
    object Idle : SpeakingState()
    object Speaking : SpeakingState()
    object Paused : SpeakingState()
}

sealed class TtsEvent {
    object Started : TtsEvent()
    object Completed : TtsEvent()
    data class Error(val message: String) : TtsEvent()
    data class FallbackToText(val text: String) : TtsEvent()
}

