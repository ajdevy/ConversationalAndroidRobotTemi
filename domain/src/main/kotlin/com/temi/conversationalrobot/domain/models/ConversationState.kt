package com.temi.conversationalrobot.domain.models

sealed class ConversationState {
    object Idle : ConversationState()
    object Listening : ConversationState()
    object Thinking : ConversationState()
    object Speaking : ConversationState()
    data class Error(val message: String) : ConversationState()
}

