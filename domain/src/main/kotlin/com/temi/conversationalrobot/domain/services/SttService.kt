package com.temi.conversationalrobot.domain.services

import kotlinx.coroutines.flow.Flow

interface SttService {
    val transcriptionResults: Flow<SttResult>
    fun startListening()
    fun stopListening()
    fun isListening(): Boolean
}

sealed class SttResult {
    data class Success(val text: String) : SttResult()
    data class Error(val message: String) : SttResult()
    object Timeout : SttResult()
}




