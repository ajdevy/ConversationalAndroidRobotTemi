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
class EndToEndConversationTest : BaseTest(), KoinComponent {
    
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
    
    @Test(timeout = 60000) // 60 second timeout
    fun verifyCompleteConversationFlowWithUserQuery() {
        
            step("Activate conversation manually") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
            }
            
            step("Process user query") {
                runBlocking {
                    delay(500) // Wait for coroutines to start collecting
                    // Set LLM response before STT emits
                    (llmService as MockLlmService).emitSuccess("Our menu includes delicious pasta dishes, fresh salads, and mouth-watering desserts.")
                    delay(100)
                    (sttService as MockSttService).emitSuccess("What's on the menu?")
                    delay(300) // Wait for state update (STT -> Thinking -> LLM -> Speaking)
                }
            }
            
            step("Speak response") {
                runBlocking {
                    (ttsService as MockTtsService).emitCompleted()
                    delay(2000) // Wait for state update: TTS completed -> Idle -> UI state update -> button enabled - increased delay
                }
            }
            
            step("Complete conversation") {
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

