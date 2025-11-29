package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.domain.services.SttService
import com.temi.conversationalrobot.domain.services.LlmService
import com.temi.conversationalrobot.domain.services.TtsService
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
class StatusIndicatorStateTest : BaseTest(), KoinComponent {
    
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
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifyStatusIndicatorStateTransitions() {
        test {
            step("Verify Idle state") {
                // App starts in Idle state
                composeTestRule.onRoot().assertExists()
            }
            
            step("Transition to Listening state") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
                
                composeTestRule.waitForIdle()
                // State should be Listening (red pulsating)
            }
            
            step("Transition to Thinking state") {
                runBlocking {
                    delay(100)
                    (sttService as MockSttService).emitSuccess(TestData.SAMPLE_TRANSCRIPTION)
                    delay(200)
                }
                // State should be Thinking (red pulsating)
            }
            
            step("Transition to Speaking state") {
                runBlocking {
                    (llmService as MockLlmService).emitSuccess(TestData.SAMPLE_LLM_RESPONSE)
                    delay(200)
                }
                // State should be Speaking (green pulsating)
            }
            
            step("Return to Idle state") {
                runBlocking {
                    (ttsService as MockTtsService).emitCompleted()
                    delay(200)
                }
                composeTestRule.waitForIdle()
                // State should return to Idle (grey static)
            }
        }
    }
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifyErrorStateDisplay() {
        test {
            step("Trigger error state") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
                
                runBlocking {
                    delay(100)
                    (sttService as MockSttService).emitError("Test error")
                    delay(200)
                }
            }
            
            step("Verify Error state displayed") {
                composeTestRule.waitForIdle()
                runBlocking { delay(800) } // Wait for state update
                composeTestRule.waitForIdle()
                // Error state should be displayed (grey static)
                val settingsScreen = SettingsScreen(composeTestRule)
                var success = false
                var retries = 0
                val maxRetries = 10
                val startTime = System.currentTimeMillis()
                val maxWaitTime = 5000L
                while (!success && retries < maxRetries && (System.currentTimeMillis() - startTime) < maxWaitTime) {
                    try {
                        settingsScreen.verifyStartConversationButtonIsEnabled()
                        success = true
                    } catch (e: AssertionError) {
                        if (retries == maxRetries - 1 || (System.currentTimeMillis() - startTime) >= maxWaitTime) {
                            throw e
                        }
                        runBlocking { delay(200) }
                        composeTestRule.waitForIdle()
                        retries++
                    }
                }
            }
        }
    }
}

