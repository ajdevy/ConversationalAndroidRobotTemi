package com.temi.conversationalrobot.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.domain.models.ConversationState
import com.temi.conversationalrobot.domain.usecase.ActivateConversationUseCase
import com.temi.conversationalrobot.domain.usecase.HandleConversationUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val wakeWordEnabled: Boolean = true,
    val wakeWordSensitivity: Int = 1,
    val ttsSpeechRate: Float = 150f,
    val debugInfoEnabled: Boolean = false,
    val statusIndicatorSize: Float = 0.6f,
    val interruptionDetectionEnabled: Boolean = true,
    val canActivate: Boolean = true
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val activateConversationUseCase: ActivateConversationUseCase,
    private val handleConversationUseCase: HandleConversationUseCase
) : ViewModel() {
    
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.getSettings(),
        handleConversationUseCase.conversationState
    ) { settings, conversationState ->
        val canActivate = conversationState is ConversationState.Idle || conversationState is ConversationState.Error
        android.util.Log.d("TestDebug", "SettingsViewModel: conversationState=$conversationState, canActivate=$canActivate")
        SettingsUiState(
            wakeWordEnabled = settings.wakeWordEnabled,
            wakeWordSensitivity = settings.wakeWordSensitivity,
            ttsSpeechRate = settings.ttsSpeechRate,
            debugInfoEnabled = settings.debugInfoEnabled,
            statusIndicatorSize = settings.statusIndicatorSize,
            interruptionDetectionEnabled = settings.interruptionDetectionEnabled,
            canActivate = canActivate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )
    
    fun updateWakeWordEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateWakeWordEnabled(enabled)
        }
    }
    
    fun updateWakeWordSensitivity(sensitivity: Int) {
        viewModelScope.launch {
            settingsRepository.updateWakeWordSensitivity(sensitivity)
        }
    }
    
    fun updateTtsSpeechRate(rate: Float) {
        viewModelScope.launch {
            settingsRepository.updateTtsSpeechRate(rate)
        }
    }
    
    fun updateDebugInfo(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDebugInfo(enabled)
        }
    }
    
    fun updateStatusIndicatorSize(size: Float) {
        viewModelScope.launch {
            settingsRepository.updateStatusIndicatorSize(size)
        }
    }
    
    fun updateInterruptionDetection(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateInterruptionDetection(enabled)
        }
    }
    
    fun activateConversation() {
        activateConversationUseCase.activate()
    }
}

