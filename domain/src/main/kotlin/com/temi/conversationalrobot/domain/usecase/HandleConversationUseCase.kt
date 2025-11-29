package com.temi.conversationalrobot.domain.usecase

import com.temi.conversationalrobot.domain.models.ConversationState
import com.temi.conversationalrobot.domain.services.LlmResult
import com.temi.conversationalrobot.domain.services.LlmService
import com.temi.conversationalrobot.domain.services.SttResult
import com.temi.conversationalrobot.domain.services.SttService
import com.temi.conversationalrobot.domain.services.TtsEvent
import com.temi.conversationalrobot.domain.services.TtsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface HandleConversationUseCase {
    val conversationState: StateFlow<ConversationState>
    fun activate()
    fun handleConversation()
}

class HandleConversationUseCaseImpl(
    private val sttService: SttService,
    private val llmService: LlmService,
    private val ttsService: TtsService
) : HandleConversationUseCase {
    
    private val _conversationState = MutableStateFlow<ConversationState>(ConversationState.Idle)
    override val conversationState: StateFlow<ConversationState> = _conversationState.asStateFlow()
    
    private val useCaseScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun activate() {
        val oldState = _conversationState.value
        _conversationState.value = ConversationState.Listening
        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $oldState -> Listening")
        sttService.startListening()
        
        useCaseScope.launch {
            android.util.Log.d("TestDebug", "HandleConversationUseCase: STT collector started")
            sttService.transcriptionResults.collect { result ->
                when (result) {
                    is SttResult.Success -> {
                        val previousState = _conversationState.value
                        _conversationState.value = ConversationState.Thinking
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $previousState -> Thinking (STT success)")
                        llmService.generateResponse(result.text)
                    }
                    is SttResult.Error -> {
                        val previousState = _conversationState.value
                        _conversationState.value = ConversationState.Error(result.message)
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $previousState -> Error(${result.message}) (STT error)")
                    }
                    is SttResult.Timeout -> {
                        val previousState = _conversationState.value
                        _conversationState.value = ConversationState.Error("No speech detected")
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $previousState -> Error(No speech detected) (STT timeout)")
                    }
                }
            }
        }
        
        useCaseScope.launch {
            android.util.Log.d("TestDebug", "HandleConversationUseCase: LLM collector started")
            llmService.responses.collect { result ->
                when (result) {
                    is LlmResult.Success -> {
                        val previousState = _conversationState.value
                        _conversationState.value = ConversationState.Speaking
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $previousState -> Speaking (LLM success)")
                        ttsService.speak(result.text)
                    }
                    is LlmResult.Error -> {
                        val previousState = _conversationState.value
                        _conversationState.value = ConversationState.Error(result.message)
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $previousState -> Error(${result.message}) (LLM error)")
                    }
                    is LlmResult.Timeout -> {
                        val previousState = _conversationState.value
                        _conversationState.value = ConversationState.Error("Response timeout")
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $previousState -> Error(Response timeout) (LLM timeout)")
                    }
                }
            }
        }
        
        useCaseScope.launch {
            android.util.Log.d("TestDebug", "HandleConversationUseCase: TTS collector started")
            ttsService.ttsEvents.collect { event ->
                android.util.Log.d("TestDebug", "HandleConversationUseCase: TTS event received: $event")
                when (event) {
                    is TtsEvent.Completed -> {
                        val previousState = _conversationState.value
                        _conversationState.value = ConversationState.Idle
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $previousState -> Idle (TTS completed)")
                    }
                    is TtsEvent.Error -> {
                        val previousState = _conversationState.value
                        _conversationState.value = ConversationState.Error(event.message)
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: State $previousState -> Error(${event.message}) (TTS error)")
                    }
                    else -> {
                        android.util.Log.d("TestDebug", "HandleConversationUseCase: TTS event $event (no state change)")
                    }
                }
            }
        }
    }
    
    override fun handleConversation() {
        activate()
    }
}

