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
class MultipleConversationsTest : BaseTest(), KoinComponent {
    
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
    fun verifyMultipleConversationCycles() {
        test {
            step("Complete first conversation cycle") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
                
                runBlocking {
                    delay(500) // Wait for coroutines to start collecting
                    // Set LLM response before STT emits
                    (llmService as MockLlmService).emitSuccess("First response")
                    delay(100)
                    (sttService as MockSttService).emitSuccess("First question")
                    delay(300) // Wait for state update (STT -> Thinking -> LLM -> Speaking)
                    (ttsService as MockTtsService).emitCompleted()
                    delay(800) // Wait for state update: TTS completed -> Idle
                }
            }
            
            step("Verify return to Idle state") {
                composeTestRule.waitForIdle()
                runBlocking { delay(800) } // Wait for state update
                composeTestRule.waitForIdle()
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
            
            step("Start second conversation immediately") {
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
                
                runBlocking {
                    delay(100)
                    (llmService as MockLlmService).emitSuccess("Second response")
                    delay(100)
                    (sttService as MockSttService).emitSuccess("Second question")
                    delay(300)
                    (ttsService as MockTtsService).emitCompleted()
                    delay(800)
                }
            }
            
            step("Verify second conversation flows correctly") {
                composeTestRule.waitForIdle()
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
            
            step("Complete third conversation cycle") {
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickStartConversationButton()
                
                runBlocking {
                    delay(100)
                    (llmService as MockLlmService).emitSuccess("Third response")
                    delay(100)
                    (sttService as MockSttService).emitSuccess("Third question")
                    delay(300)
                    (ttsService as MockTtsService).emitCompleted()
                    delay(800)
                }
                
                composeTestRule.waitForIdle()
                val finalSettingsScreen = SettingsScreen(composeTestRule)
                var success = false
                var retries = 0
                val maxRetries = 10
                val startTime = System.currentTimeMillis()
                val maxWaitTime = 5000L
                while (!success && retries < maxRetries && (System.currentTimeMillis() - startTime) < maxWaitTime) {
                    try {
                        finalSettingsScreen.verifyStartConversationButtonIsEnabled()
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

