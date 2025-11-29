package com.temi.conversationalrobot.domain.usecase

import com.temi.conversationalrobot.domain.models.ConversationState

interface ActivateConversationUseCase {
    fun activate()
}

class ActivateConversationUseCaseImpl(
    private val handleConversationUseCase: HandleConversationUseCase
) : ActivateConversationUseCase {
    
    override fun activate() {
        handleConversationUseCase.activate()
    }
}

