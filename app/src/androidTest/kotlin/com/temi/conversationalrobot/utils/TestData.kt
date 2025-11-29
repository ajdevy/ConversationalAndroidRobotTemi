package com.temi.conversationalrobot.utils

import com.temi.conversationalrobot.domain.services.TtsService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object TestData {
    const val SAMPLE_TRANSCRIPTION = "What's on the menu?"
    const val SAMPLE_LLM_RESPONSE = "Our menu includes delicious pasta dishes, fresh salads, and mouth-watering desserts."
    const val SAMPLE_ERROR_MESSAGE = "Test error message"
    
    val sampleTranscriptions = listOf(
        "What's on the menu?",
        "Do you have vegetarian options?",
        "What are your hours?",
        "Tell me about the pasta"
    )
    
    val sampleLlmResponses = listOf(
        "Our menu includes delicious pasta dishes, fresh salads, and mouth-watering desserts.",
        "Yes, we have several vegetarian options including our garden salad and margherita pizza.",
        "We are open Monday through Friday from 11am to 10pm.",
        "Our pasta is made fresh daily and includes options like fettuccine alfredo and spaghetti carbonara."
    )
    
    /**
     * Test helper to reset conversation state to Idle by ensuring TTS collector is active
     * and emitting Completed event. This is useful for error recovery scenarios in tests.
     */
    fun resetConversationStateToIdle(ttsService: TtsService) {
        runBlocking {
            // Ensure TTS collector is active by calling speak first
            if (ttsService is MockTtsService) {
                ttsService.speak("dummy")
                delay(100) // Small delay to ensure collector is active
                // Emit Completed to reset state to Idle
                ttsService.emitCompleted()
                delay(200) // Wait for state transition
            }
        }
    }
}

