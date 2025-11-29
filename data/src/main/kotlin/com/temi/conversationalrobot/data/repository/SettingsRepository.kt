package com.temi.conversationalrobot.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.temi.conversationalrobot.domain.models.AppSettings
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(
    private val context: Context,
    private val dataStoreOverride: DataStore<Preferences>? = null
) : SettingsRepository {
    
    private val _settingsStateFlow = MutableStateFlow(AppSettings())
    override val settingsStateFlow: StateFlow<AppSettings> = _settingsStateFlow.asStateFlow()
    
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dataStore: DataStore<Preferences> = dataStoreOverride ?: context.settingsDataStore
    
    companion object {
        private val WAKE_WORD_ENABLED_KEY = booleanPreferencesKey("wake_word_enabled")
        private val WAKE_WORD_SENSITIVITY_KEY = intPreferencesKey("wake_word_sensitivity")
        private val TTS_SPEECH_RATE_KEY = floatPreferencesKey("tts_speech_rate")
        private val DEBUG_INFO_ENABLED_KEY = booleanPreferencesKey("debug_info_enabled")
        private val STATUS_INDICATOR_SIZE_KEY = floatPreferencesKey("status_indicator_size")
        private val INTERRUPTION_DETECTION_ENABLED_KEY = booleanPreferencesKey("interruption_detection_enabled")
    }
    
    override fun getSettings(): Flow<AppSettings> {
        return dataStore.data.map { preferences ->
            AppSettings(
                wakeWordEnabled = preferences[WAKE_WORD_ENABLED_KEY] ?: true,
                wakeWordSensitivity = preferences[WAKE_WORD_SENSITIVITY_KEY] ?: 1,
                ttsSpeechRate = preferences[TTS_SPEECH_RATE_KEY] ?: 150f,
                debugInfoEnabled = preferences[DEBUG_INFO_ENABLED_KEY] ?: false,
                statusIndicatorSize = preferences[STATUS_INDICATOR_SIZE_KEY] ?: 0.6f,
                interruptionDetectionEnabled = preferences[INTERRUPTION_DETECTION_ENABLED_KEY] ?: true
            )
        }
    }
    
    override suspend fun updateWakeWordEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[WAKE_WORD_ENABLED_KEY] = enabled
        }
        updateStateFlow()
    }
    
    override suspend fun updateWakeWordSensitivity(sensitivity: Int) {
        dataStore.edit { preferences ->
            preferences[WAKE_WORD_SENSITIVITY_KEY] = sensitivity.coerceIn(0, 2)
        }
        updateStateFlow()
    }
    
    override suspend fun updateTtsSpeechRate(rate: Float) {
        dataStore.edit { preferences ->
            preferences[TTS_SPEECH_RATE_KEY] = rate.coerceIn(50f, 250f)
        }
        updateStateFlow()
    }
    
    override suspend fun updateDebugInfo(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DEBUG_INFO_ENABLED_KEY] = enabled
        }
        updateStateFlow()
    }
    
    override suspend fun updateStatusIndicatorSize(size: Float) {
        dataStore.edit { preferences ->
            preferences[STATUS_INDICATOR_SIZE_KEY] = size.coerceIn(0.4f, 0.8f)
        }
        updateStateFlow()
    }
    
    override suspend fun updateInterruptionDetection(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[INTERRUPTION_DETECTION_ENABLED_KEY] = enabled
        }
        updateStateFlow()
    }
    
    private suspend fun updateStateFlow() {
        val settings = getSettings().first()
        _settingsStateFlow.value = settings
    }
    
    init {
        // Initialize StateFlow with current settings
        repositoryScope.launch {
            getSettings().collect { settings ->
                _settingsStateFlow.value = settings
            }
        }
    }
}

