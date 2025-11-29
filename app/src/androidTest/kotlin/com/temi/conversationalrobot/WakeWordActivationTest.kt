package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.data.service.WakeWordDetector
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
class WakeWordActivationTest : BaseTest(), KoinComponent {
    
    companion object {
        @org.junit.BeforeClass
        @JvmStatic
        fun setupKoin() {
            BaseTest.setupKoinForTests()
        }
    }
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    private val wakeWordDetector: WakeWordDetector by inject()
    private val sttService: SttService by inject()
    private val llmService: LlmService by inject()
    private val ttsService: TtsService by inject()
    private val settingsRepository: com.temi.conversationalrobot.domain.repository.SettingsRepository by inject()
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifyWakeWordActivationFlow() {
        
            step("Enable wake word in settings") {
                runBlocking {
                    settingsRepository.updateWakeWordEnabled(true)
                    delay(100) // Wait for settings to propagate
                }
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                composeTestRule.waitForIdle()
            }
            
            step("Simulate wake word detection") {
                runBlocking {
                    (wakeWordDetector as MockWakeWordDetector).emitDetection()
                    delay(500) // Wait for activation - increased delay to allow state to propagate
                }
            }
            
            step("Verify conversation activates automatically") {
                composeTestRule.waitForIdle()
                runBlocking { delay(500) } // Wait for activation - increased delay
                composeTestRule.waitForIdle()
                val settingsScreen = SettingsScreen(composeTestRule)
                // Button should be disabled during conversation - check after state propagates
                try {
                    settingsScreen.verifyStartConversationButtonIsDisabled()
                } catch (e: AssertionError) {
                    // If button is still enabled, wait a bit more for state to propagate
                    runBlocking { delay(300) }
                    composeTestRule.waitForIdle()
                    settingsScreen.verifyStartConversationButtonIsDisabled()
                }
            }
            
            step("Mock STT transcription result") {
                runBlocking {
                    delay(100)
                    // Emit STT success first, then set LLM response
                    (sttService as MockSttService).emitSuccess(TestData.SAMPLE_TRANSCRIPTION)
                    delay(100)
                    (llmService as MockLlmService).emitSuccess(TestData.SAMPLE_LLM_RESPONSE)
                    delay(300) // Wait for LLM to process
                }
            }
            
            step("Mock TTS completion") {
                runBlocking {
                    (ttsService as MockTtsService).emitCompleted()
                    delay(2000) // Wait for state update - increased delay
                }
            }
            
            step("Verify return to Idle state") {
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
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifyWakeWordDisabledScenario() {
        
            step("Disable wake word in settings") {
                runBlocking {
                    settingsRepository.updateWakeWordEnabled(false)
                    delay(100) // Wait for settings to propagate
                }
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
                
                val settingsScreen = SettingsScreen(composeTestRule)
                composeTestRule.waitForIdle()
            }
            
            step("Simulate wake word detection") {
                runBlocking {
                    (wakeWordDetector as MockWakeWordDetector).emitDetection()
                    delay(200)
                }
            }
            
            step("Verify conversation does not activate") {
                val settingsScreen = SettingsScreen(composeTestRule)
                composeTestRule.waitForIdle()
                // Button should remain enabled if wake word is disabled
                settingsScreen.verifyStartConversationButtonIsEnabled()
            }
    }
}

