package com.temi.conversationalrobot.domain.services

import kotlinx.coroutines.flow.Flow

interface LlmService {
    val responses: Flow<LlmResult>
    suspend fun generateResponse(userUtterance: String)
    suspend fun initialize()
}

sealed class LlmResult {
    data class Success(val text: String) : LlmResult()
    data class Error(val message: String) : LlmResult()
    object Timeout : LlmResult()
}




