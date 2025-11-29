package com.temi.conversationalrobot.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule

class SettingsScreen(private val composeTestRule: ComposeTestRule) {
    
    val title: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText("Settings")
    
    val backButton: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithContentDescription("Back")
    
    val startConversationButton: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText("Start Conversation")
    
    val wakeWordSwitch: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithText("Wake Word")
    
    fun clickBackButton() {
        backButton.performClick()
    }
    
    fun clickStartConversationButton() {
        startConversationButton.performClick()
    }
    
    fun verifyTitleIsDisplayed() {
        title.assertIsDisplayed()
    }
    
    fun verifyStartConversationButtonIsDisplayed() {
        startConversationButton.assertIsDisplayed()
    }
    
    fun verifyStartConversationButtonIsEnabled() {
        startConversationButton.assertIsEnabled()
    }
    
    fun verifyStartConversationButtonIsDisabled() {
        composeTestRule.waitForIdle()
        // Simple assertion - if it fails, the test will show the actual state
        startConversationButton.assertIsNotEnabled()
    }
    
    fun verifyActivationSectionIsDisplayed() {
        composeTestRule.onNodeWithText("Activation").assertIsDisplayed()
    }
    
    fun verifySpeechSectionIsDisplayed() {
        composeTestRule.onNodeWithText("Speech").assertIsDisplayed()
    }
    
    fun verifyDisplaySectionIsDisplayed() {
        composeTestRule.onNodeWithText("Display").assertIsDisplayed()
    }
    
    fun verifyDebugSectionIsDisplayed() {
        composeTestRule.onNodeWithText("Debug").assertIsDisplayed()
    }
}

