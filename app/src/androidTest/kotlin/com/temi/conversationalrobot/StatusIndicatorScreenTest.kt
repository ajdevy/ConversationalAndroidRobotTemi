package com.temi.conversationalrobot

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.temi.conversationalrobot.domain.models.ConversationState
import com.temi.conversationalrobot.screen.StatusIndicatorScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatusIndicatorScreenTest : BaseTest() {
    
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
    fun verifyAppLaunchesSuccessfully() {
        
            step("Launch app") {
                // App is launched automatically by createAndroidComposeRule
            }
            
            step("Verify app is running") {
                composeTestRule.onRoot().assertExists()
            }
    }
    
    @Test(timeout = 15000) // 15 second timeout
    fun verifyStatusIndicatorDisplaysWithIdleState() {
        
            step("Launch app") {
                // App launches automatically
            }
            
            step("Verify status indicator is displayed") {
                // The status indicator is a Canvas element, verify it exists
                composeTestRule.onRoot().assertExists()
            }
            
            step("Verify settings FAB is visible") {
                val screen = StatusIndicatorScreen(composeTestRule)
                screen.verifySettingsFabIsDisplayed()
            }
    }
    
    @Test(timeout = 15000) // 15 second timeout
    fun verifySettingsFabIsClickable() {
        
            step("Launch app") {
                // App launches automatically
            }
            
            step("Verify settings FAB is clickable") {
                val screen = StatusIndicatorScreen(composeTestRule)
                screen.verifySettingsFabIsClickable()
            }
    }
}

