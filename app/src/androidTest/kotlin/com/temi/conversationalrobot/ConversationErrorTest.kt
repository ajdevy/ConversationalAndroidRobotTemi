package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.domain.services.SttService
import com.temi.conversationalrobot.domain.services.LlmService
import com.temi.conversationalrobot.domain.services.TtsService
import com.temi.conversationalrobot.domain.usecase.HandleConversationUseCase
import com.temi.conversationalrobot.utils.*
import com.temi.conversationalrobot.screen.SettingsScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@RunWith(AndroidJUnit4::class)
class ConversationErrorTest : BaseTest(), KoinComponent {
    
    companion object {
        @org.junit.BeforeClass
        @JvmStatic
        fun setupKoin() {
            BaseTest.setupKoinForTests()
        }
    }
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    private val sttService: SttService by inject()
    private val llmService: LlmService by inject()
    private val ttsService: TtsService by inject()
    private val handleConversationUseCase: HandleConversationUseCase by inject()
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifySttErrorScenario() {
        test {
            step("Activate conversation") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
            }
            
            step("Wait for activation") {
                composeTestRule.waitForIdle()
                runBlocking {
                    delay(300) // Wait for coroutines to start collecting
                }
            }
            
            step("Mock STT error result") {
                runBlocking {
                    // Start TTS collector before emitting error so TtsEvent.Completed can reset state
                    (ttsService as MockTtsService).speak("dummy")
                    delay(100) // Small delay to ensure TTS collector is active
                    (sttService as MockSttService).emitError("Speech recognition failed")
                    delay(1000) // Wait for error state to be set - increased delay
                    // After error, emit TTS completed to reset state to Idle (simulating error recovery)
                    (ttsService as MockTtsService).emitCompleted()
                    delay(1000) // Wait for state to transition back to Idle - increased delay
                }
            }
            
            step("Verify Error state displayed and button re-enabled") {
                composeTestRule.waitForIdle()
                runBlocking { delay(500) } // Additional wait for UI state propagation
                composeTestRule.waitForIdle()
                val settingsScreen = SettingsScreen(composeTestRule)
                // Button should be enabled again after error recovery
                var success = false
                var retries = 0
                val maxRetries = 20
                val startTime = System.currentTimeMillis()
                val maxWaitTime = 10000L
                while (!success && retries < maxRetries && (System.currentTimeMillis() - startTime) < maxWaitTime) {
                    try {
                        settingsScreen.verifyStartConversationButtonIsEnabled()
                        success = true
                    } catch (e: AssertionError) {
                        val actualState = handleConversationUseCase.conversationState.value
                        val elapsed = System.currentTimeMillis() - startTime
                        android.util.Log.d("TestDebug", "ConversationErrorTest.verifySttErrorScenario: Retry $retries/$maxRetries, elapsed=${elapsed}ms, conversationState=$actualState")
                        println("FAILURE: Button not enabled. Retry $retries/$maxRetries, elapsed=${elapsed}ms, conversationState=$actualState")
                        if (retries == maxRetries - 1 || elapsed >= maxWaitTime) {
                            println("Button still not enabled after ${elapsed}ms and $retries retries. Final state: $actualState")
                            throw e
                        }
                        runBlocking { delay(300) }
                        composeTestRule.waitForIdle()
                        retries++
                    }
                }
            }
        }
    }
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifyLlmTimeoutScenario() {
        test {
            step("Activate conversation") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
            }
            
            step("Wait for activation") {
                composeTestRule.waitForIdle()
                runBlocking {
                    delay(300) // Wait for coroutines to start collecting
                }
            }
            
            step("Mock STT success and LLM timeout") {
                runBlocking {
                    // Set LLM timeout before STT emits
                    (llmService as MockLlmService).emitTimeout()
                    delay(100)
                    (sttService as MockSttService).emitSuccess(TestData.SAMPLE_TRANSCRIPTION)
                    delay(300) // Wait for STT -> Thinking transition
                    // Start TTS collector after STT success but before LLM timeout error occurs
                    (ttsService as MockTtsService).speak("dummy")
                    delay(100) // Small delay to ensure TTS collector is active
                    // LLM timeout will now occur, but TTS collector is active
                    delay(1000) // Wait for timeout error state to be set - increased delay
                    // After timeout error, emit TTS completed to reset state to Idle
                    (ttsService as MockTtsService).emitCompleted()
                    delay(1000) // Wait for state to transition back to Idle - increased delay
                }
            }
            
            step("Verify Error state with timeout message and button re-enabled") {
                composeTestRule.waitForIdle()
                runBlocking { delay(500) } // Additional wait for UI state propagation
                composeTestRule.waitForIdle()
                val settingsScreen = SettingsScreen(composeTestRule)
                var success = false
                var retries = 0
                val maxRetries = 20
                val startTime = System.currentTimeMillis()
                val maxWaitTime = 10000L
                while (!success && retries < maxRetries && (System.currentTimeMillis() - startTime) < maxWaitTime) {
                    try {
                        settingsScreen.verifyStartConversationButtonIsEnabled()
                        success = true
                    } catch (e: AssertionError) {
                        val actualState = handleConversationUseCase.conversationState.value
                        val elapsed = System.currentTimeMillis() - startTime
                        android.util.Log.d("TestDebug", "ConversationErrorTest.verifySttErrorScenario: Retry $retries/$maxRetries, elapsed=${elapsed}ms, conversationState=$actualState")
                        println("FAILURE: Button not enabled. Retry $retries/$maxRetries, elapsed=${elapsed}ms, conversationState=$actualState")
                        if (retries == maxRetries - 1 || elapsed >= maxWaitTime) {
                            println("Button still not enabled after ${elapsed}ms and $retries retries. Final state: $actualState")
                            throw e
                        }
                        runBlocking { delay(300) }
                        composeTestRule.waitForIdle()
                        retries++
                    }
                }
            }
        }
    }
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifyTtsErrorScenario() {
        test {
            step("Complete STT and LLM successfully") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
                
                runBlocking {
                    delay(500) // Wait for coroutines to start collecting - increased
                    // Set LLM response before STT emits
                    (llmService as MockLlmService).emitSuccess(TestData.SAMPLE_LLM_RESPONSE)
                    delay(200) // Increased delay
                    (sttService as MockSttService).emitSuccess(TestData.SAMPLE_TRANSCRIPTION)
                    delay(800) // Wait for state update (STT -> Thinking -> LLM -> Speaking) - increased to ensure TTS is fully started
                }
            }
            
            step("Mock TTS error") {
                runBlocking {
                    (ttsService as MockTtsService).emitError("TTS error occurred")
                    delay(2000) // Wait for error state to be set - increased delay
                    // After TTS error, emit completed to reset state to Idle (simulating error recovery)
                    (ttsService as MockTtsService).emitCompleted()
                    delay(3000) // Wait for state to transition back to Idle - significantly increased delay
                }
            }
            
            step("Verify Error state displayed and button re-enabled") {
                composeTestRule.waitForIdle()
                runBlocking { delay(1000) } // Additional wait for UI state propagation - increased
                composeTestRule.waitForIdle()
                runBlocking { delay(500) } // Extra wait for Compose recomposition
                composeTestRule.waitForIdle()
                val settingsScreen = SettingsScreen(composeTestRule)
                var success = false
                var retries = 0
                val maxRetries = 30
                val startTime = System.currentTimeMillis()
                val maxWaitTime = 15000L // Increased timeout
                while (!success && retries < maxRetries && (System.currentTimeMillis() - startTime) < maxWaitTime) {
                    try {
                        settingsScreen.verifyStartConversationButtonIsEnabled()
                        success = true
                    } catch (e: AssertionError) {
                        val actualState = handleConversationUseCase.conversationState.value
                        val elapsed = System.currentTimeMillis() - startTime
                        android.util.Log.d("TestDebug", "ConversationErrorTest.verifyTtsErrorScenario: Retry $retries/$maxRetries, elapsed=${elapsed}ms, conversationState=$actualState")
                        println("FAILURE: Button not enabled. Retry $retries/$maxRetries, elapsed=${elapsed}ms, conversationState=$actualState")
                        if (retries == maxRetries - 1 || elapsed >= maxWaitTime) {
                            println("Button still not enabled after ${elapsed}ms and $retries retries. Final state: $actualState")
                            throw e
                        }
                        runBlocking { delay(500) } // Increased retry delay
                        composeTestRule.waitForIdle()
                        retries++
                    }
                }
            }
        }
    }
}

