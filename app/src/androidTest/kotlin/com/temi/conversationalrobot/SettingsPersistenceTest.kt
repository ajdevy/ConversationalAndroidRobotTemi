package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.domain.models.AppSettings
import com.temi.conversationalrobot.screen.SettingsScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@RunWith(AndroidJUnit4::class)
class SettingsPersistenceTest : BaseTest(), KoinComponent {
    
    companion object {
        @org.junit.BeforeClass
        @JvmStatic
        fun setupKoin() {
            BaseTest.setupKoinForTests()
        }
    }
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    private val settingsRepository: SettingsRepository by inject()
    
    @Test(timeout = 30000) // 30 second timeout
    fun verifySettingsConfigurationPersistence() {
        
            step("Navigate to settings") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
            }
            
            step("Modify settings") {
                val settingsScreen = SettingsScreen(composeTestRule)
                
                runBlocking {
                    // Toggle wake word
                    settingsRepository.updateWakeWordEnabled(false)
                    
                    // Adjust wake word sensitivity
                    settingsRepository.updateWakeWordSensitivity(2) // High
                    
                    // Adjust TTS speech rate
                    settingsRepository.updateTtsSpeechRate(200f)
                    
                    // Adjust status indicator size
                    settingsRepository.updateStatusIndicatorSize(0.7f)
                    
                    // Toggle debug info
                    settingsRepository.updateDebugInfo(true)
                    
                    // Toggle interruption detection
                    settingsRepository.updateInterruptionDetection(false)
                }
            }
            
            step("Navigate back and return to settings") {
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickBackButton()
                
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
            }
            
            step("Verify all settings persist correctly") {
                runBlocking {
                    val settings = settingsRepository.getSettings().first()
                    assert(settings.wakeWordEnabled == false)
                    assert(settings.wakeWordSensitivity == 2)
                    assert(settings.ttsSpeechRate == 200f)
                    assert(settings.statusIndicatorSize == 0.7f)
                    assert(settings.debugInfoEnabled == true)
                    assert(settings.interruptionDetectionEnabled == false)
                }
            }
    }
}

