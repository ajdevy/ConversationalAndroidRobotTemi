package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.screen.StatusIndicatorScreen
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupVerificationTest : BaseTest() {
    
    companion object {
        @BeforeClass
        @JvmStatic
        fun setupKoin() {
            BaseTest.setupKoinForTests()
        }
    }
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test(timeout = 15000) // 15 second timeout
    fun verifyAppLaunchesAndMainScreenIsDisplayed() {
            step("Launch app") {
                // App is launched automatically by createAndroidComposeRule
                // Just verify we're on the main screen
            }
            
            step("Verify settings FAB is visible") {
                val screen = StatusIndicatorScreen(composeTestRule)
                screen.verifySettingsFabIsDisplayed()
            }
            
            step("Verify settings FAB is clickable") {
                val screen = StatusIndicatorScreen(composeTestRule)
                screen.verifySettingsFabIsClickable()
            }
    }
}

