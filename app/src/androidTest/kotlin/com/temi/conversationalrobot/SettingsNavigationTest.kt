package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.screen.SettingsScreen
import com.temi.conversationalrobot.screen.StatusIndicatorScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsNavigationTest : BaseTest() {
    
    companion object {
        @org.junit.BeforeClass
        @JvmStatic
        fun setupKoin() {
            BaseTest.setupKoinForTests()
        }
    }
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test(timeout = 15000) // 15 second timeout
    fun verifySettingsScreenNavigation() {
        
            step("Launch app") {
                // App launches automatically
            }
            
            step("Navigate to settings") {
                val mainScreen = StatusIndicatorScreen(composeTestRule)
                mainScreen.clickSettingsFab()
            }
            
            step("Verify settings screen displayed") {
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.verifyTitleIsDisplayed()
                settingsScreen.verifyActivationSectionIsDisplayed()
                settingsScreen.verifySpeechSectionIsDisplayed()
                settingsScreen.verifyDisplaySectionIsDisplayed()
                settingsScreen.verifyDebugSectionIsDisplayed()
            }
            
            step("Navigate back") {
                val settingsScreen = SettingsScreen(composeTestRule)
                settingsScreen.clickBackButton()
            }
            
            step("Verify return to main screen") {
                val mainScreen = StatusIndicatorScreen(composeTestRule)
                mainScreen.verifySettingsFabIsDisplayed()
            }
    }
}

