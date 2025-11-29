package com.temi.conversationalrobot.domain.services

import kotlinx.coroutines.flow.Flow

interface InterruptionHandler {
    val interruptionEvents: Flow<InterruptionEvent>
    fun startMonitoring()
    fun stopMonitoring()
}

sealed class InterruptionEvent {
    object UserSpeechDetected : InterruptionEvent()
}




