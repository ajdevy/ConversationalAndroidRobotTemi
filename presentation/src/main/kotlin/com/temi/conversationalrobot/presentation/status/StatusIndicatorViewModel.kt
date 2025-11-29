package com.temi.conversationalrobot.presentation.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.temi.conversationalrobot.data.service.WakeWordDetector
import com.temi.conversationalrobot.data.service.WakeWordDetectionEvent
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.domain.models.ConversationState
import com.temi.conversationalrobot.domain.usecase.HandleConversationUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StatusIndicatorUiState(
    val currentState: ConversationState = ConversationState.Idle,
    val indicatorSize: Float = 0.6f,
    val debugInfoEnabled: Boolean = false,
    val transcriptionText: String = "",
    val llmResponseText: String = ""
)

class StatusIndicatorViewModel(
    private val handleConversationUseCase: HandleConversationUseCase,
    private val settingsRepository: SettingsRepository,
    private val wakeWordDetector: WakeWordDetector
) : ViewModel() {
    
    val uiState: StateFlow<StatusIndicatorUiState> = combine(
        handleConversationUseCase.conversationState,
        settingsRepository.getSettings()
    ) { state, settings ->
        StatusIndicatorUiState(
            currentState = state,
            indicatorSize = settings.statusIndicatorSize,
            debugInfoEnabled = settings.debugInfoEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatusIndicatorUiState()
    )
    
    init {
        // Collect wake word detection events and activate conversation when detected
        viewModelScope.launch {
            wakeWordDetector.detectionEvents.collect { event ->
                when (event) {
                    is WakeWordDetectionEvent.Detected -> {
                        // Check if wake word is enabled and conversation is in a state that allows activation
                        val settings = settingsRepository.getSettings().first()
                        val currentState = handleConversationUseCase.conversationState.value
                        
                        if (settings.wakeWordEnabled && 
                            (currentState is ConversationState.Idle || currentState is ConversationState.Error)) {
                            android.util.Log.d("StatusIndicatorViewModel", "Wake word detected, activating conversation")
                            handleConversationUseCase.activate()
                        }
                    }
                    is WakeWordDetectionEvent.Error -> {
                        android.util.Log.e("StatusIndicatorViewModel", "Wake word detection error: ${event.message}")
                    }
                }
            }
        }
    }
}

