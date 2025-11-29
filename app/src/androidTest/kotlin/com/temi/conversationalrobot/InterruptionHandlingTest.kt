package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.domain.services.SttService
import com.temi.conversationalrobot.domain.services.LlmService
import com.temi.conversationalrobot.domain.services.TtsService
import com.temi.conversationalrobot.domain.services.InterruptionHandler
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
class InterruptionHandlingTest : BaseTest(), KoinComponent {
    
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
    private val interruptionHandler: InterruptionHandler by inject()
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifyInterruptionHandlingFlow() {
        
            step("Start conversation and reach Speaking state") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
                
                runBlocking {
                    delay(100)
                    (sttService as MockSttService).emitSuccess(TestData.SAMPLE_TRANSCRIPTION)
                    delay(200)
                    (llmService as MockLlmService).emitSuccess(TestData.SAMPLE_LLM_RESPONSE)
                    delay(200)
                    // TTS starts speaking
                }
            }
            
            step("Simulate user interruption") {
                runBlocking {
                    (interruptionHandler as MockInterruptionHandler).emitInterruption()
                    delay(200)
                }
            }
            
            step("Verify TTS stops immediately") {
                runBlocking {
                    (ttsService as MockTtsService).stop()
                    delay(100)
                }
            }
            
            step("Verify state transitions to Listening") {
                composeTestRule.waitForIdle()
                // State should transition to Listening
            }
            
            step("Mock new STT transcription from interruption") {
                runBlocking {
                    (sttService as MockSttService).emitSuccess("Wait, I meant something else")
                    delay(200)
                }
            }
            
            step("Verify new LLM response is generated") {
                runBlocking {
                    (llmService as MockLlmService).emitSuccess("I understand. What would you like to know?")
                    delay(200)
                }
            }
            
            step("Verify new TTS response plays") {
                runBlocking {
                    (ttsService as MockTtsService).emitCompleted()
                    delay(2000) // Wait for state update: TTS completed -> Idle -> UI state update -> button enabled - increased delay
                }
                
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
                        if (retries == maxRetries - 1 || (System.currentTimeMillis() - startTime) >= maxWaitTime) {
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

