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
class ConversationActivationTest : BaseTest(), KoinComponent {
    
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
    
    @Test(timeout = 60000) // 60 second timeout
    fun verifyManualConversationActivationFlow() {
        test {
            step("Navigate to settings") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
            }
            
            step("Activate conversation") {
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.verifyStartConversationButtonIsEnabled()
                settingsScreen.clickStartConversationButton()
            }
            
            step("Wait for activation and coroutines to start") {
                composeTestRule.waitForIdle()
                runBlocking {
                    delay(500) // Wait for coroutines to start collecting flows and state to propagate
                }
                composeTestRule.waitForIdle()
            }
            
            step("Mock STT success and verify transition") {
                runBlocking {
                    // Set LLM response before STT emits, so generateResponse has it ready
                    (llmService as MockLlmService).emitSuccess(TestData.SAMPLE_LLM_RESPONSE)
                    delay(200) // Increased delay to ensure pending response is set
                    (sttService as MockSttService).emitSuccess(TestData.SAMPLE_TRANSCRIPTION)
                    delay(800) // Wait for state update (STT -> Thinking -> LLM -> Speaking) - increased to ensure TTS is fully started
                }
            }
            
            step("Mock TTS completion and verify return to Idle") {
                runBlocking {
                    (ttsService as MockTtsService).emitCompleted()
                    delay(3000) // Increased delay to ensure state fully transitions to Idle and propagates through flows
                }
            }
            
            step("Verify button becomes enabled again") {
                composeTestRule.waitForIdle()
                runBlocking { delay(1000) } // Additional wait for UI state propagation - increased
                composeTestRule.waitForIdle()
                runBlocking { delay(500) } // Extra wait for Compose recomposition
                composeTestRule.waitForIdle()
                val settingsScreen = SettingsScreen(composeTestRule)
                // Retry mechanism to wait for button to become enabled (max 15 seconds total)
                var success = false
                var retries = 0
                val maxRetries = 30
                val startTime = System.currentTimeMillis()
                val maxWaitTime = 15000L // 15 seconds max - increased
                while (!success && retries < maxRetries && (System.currentTimeMillis() - startTime) < maxWaitTime) {
                    try {
                        settingsScreen.verifyStartConversationButtonIsEnabled()
                        success = true
                    } catch (e: AssertionError) {
                        val actualState = handleConversationUseCase.conversationState.value
                        val elapsed = System.currentTimeMillis() - startTime
                        android.util.Log.d("TestDebug", "ConversationActivationTest: Retry $retries/$maxRetries, elapsed=${elapsed}ms, conversationState=$actualState")
                        println("FAILURE: Button not enabled. Retry $retries/$maxRetries, elapsed=${elapsed}ms, conversationState=$actualState")
                        if (retries == maxRetries - 1 || elapsed >= maxWaitTime) {
                            // Log the error before throwing
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

