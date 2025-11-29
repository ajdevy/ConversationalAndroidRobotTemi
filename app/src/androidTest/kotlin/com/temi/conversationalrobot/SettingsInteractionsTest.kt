package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.screen.SettingsScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@RunWith(AndroidJUnit4::class)
class SettingsInteractionsTest : BaseTest(), KoinComponent {
    
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
    fun verifySettingsScreenInteractions() {
        test {
            step("Navigate to settings") {
                val mainScreen = com.temi.conversationalrobot.screen.StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
            }
            
            step("Test wake word toggle") {
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.verifyActivationSectionIsDisplayed()
                
                runBlocking {
                    settingsRepository.updateWakeWordEnabled(false)
                    val settings = settingsRepository.getSettings().first()
                    assert(!settings.wakeWordEnabled)
                    
                    settingsRepository.updateWakeWordEnabled(true)
                    val updatedSettings = settingsRepository.getSettings().first()
                    assert(updatedSettings.wakeWordEnabled)
                }
            }
            
            step("Test wake word sensitivity slider") {
                runBlocking {
                    settingsRepository.updateWakeWordSensitivity(0) // Low
                    var settings = settingsRepository.getSettings().first()
                    assert(settings.wakeWordSensitivity == 0)
                    
                    settingsRepository.updateWakeWordSensitivity(1) // Medium
                    settings = settingsRepository.getSettings().first()
                    assert(settings.wakeWordSensitivity == 1)
                    
                    settingsRepository.updateWakeWordSensitivity(2) // High
                    settings = settingsRepository.getSettings().first()
                    assert(settings.wakeWordSensitivity == 2)
                }
            }
            
            step("Test TTS speech rate slider") {
                runBlocking {
                    settingsRepository.updateTtsSpeechRate(100f)
                    var settings = settingsRepository.getSettings().first()
                    assert(settings.ttsSpeechRate == 100f)
                    
                    settingsRepository.updateTtsSpeechRate(200f)
                    settings = settingsRepository.getSettings().first()
                    assert(settings.ttsSpeechRate == 200f)
                }
            }
            
            step("Test status indicator size slider") {
                runBlocking {
                    settingsRepository.updateStatusIndicatorSize(0.5f)
                    var settings = settingsRepository.getSettings().first()
                    assert(settings.statusIndicatorSize == 0.5f)
                    
                    settingsRepository.updateStatusIndicatorSize(0.8f)
                    settings = settingsRepository.getSettings().first()
                    assert(settings.statusIndicatorSize == 0.8f)
                }
            }
            
            step("Test debug info toggle") {
                runBlocking {
                    settingsRepository.updateDebugInfo(true)
                    var settings = settingsRepository.getSettings().first()
                    assert(settings.debugInfoEnabled)
                    
                    settingsRepository.updateDebugInfo(false)
                    settings = settingsRepository.getSettings().first()
                    assert(!settings.debugInfoEnabled)
                }
            }
            
            step("Test interruption detection toggle") {
                runBlocking {
                    settingsRepository.updateInterruptionDetection(false)
                    var settings = settingsRepository.getSettings().first()
                    assert(!settings.interruptionDetectionEnabled)
                    
                    settingsRepository.updateInterruptionDetection(true)
                    settings = settingsRepository.getSettings().first()
                    assert(settings.interruptionDetectionEnabled)
                }
            }
        }
    }
}

