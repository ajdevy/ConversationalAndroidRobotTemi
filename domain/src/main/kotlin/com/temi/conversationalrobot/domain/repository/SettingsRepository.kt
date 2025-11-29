package com.temi.conversationalrobot.domain.repository

import com.temi.conversationalrobot.domain.models.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    val settingsStateFlow: StateFlow<AppSettings>
    suspend fun updateWakeWordEnabled(enabled: Boolean)
    suspend fun updateWakeWordSensitivity(sensitivity: Int)
    suspend fun updateTtsSpeechRate(rate: Float)
    suspend fun updateDebugInfo(enabled: Boolean)
    suspend fun updateStatusIndicatorSize(size: Float)
    suspend fun updateInterruptionDetection(enabled: Boolean)
}




