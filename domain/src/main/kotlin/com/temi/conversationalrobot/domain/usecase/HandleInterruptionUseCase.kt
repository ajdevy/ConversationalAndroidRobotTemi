package com.temi.conversationalrobot.domain.usecase

import com.temi.conversationalrobot.domain.models.ConversationState
import com.temi.conversationalrobot.domain.services.InterruptionEvent
import com.temi.conversationalrobot.domain.services.InterruptionHandler
import com.temi.conversationalrobot.domain.services.SttService
import com.temi.conversationalrobot.domain.services.TtsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface HandleInterruptionUseCase {
    fun initialize()
}

class HandleInterruptionUseCaseImpl(
    private val interruptionHandler: InterruptionHandler,
    private val ttsService: TtsService,
    private val sttService: SttService,
    private val conversationState: StateFlow<ConversationState>
) : HandleInterruptionUseCase {
    
    private val useCaseScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun initialize() {
        useCaseScope.launch {
            interruptionHandler.interruptionEvents.collect { event ->
                when (event) {
                    is InterruptionEvent.UserSpeechDetected -> {
                        ttsService.stop()
                        sttService.startListening()
                    }
                }
            }
        }
    }
}

