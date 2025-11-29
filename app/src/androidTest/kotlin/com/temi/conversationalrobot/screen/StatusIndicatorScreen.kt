package com.temi.conversationalrobot.screen

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule

class StatusIndicatorScreen(private val composeTestRule: ComposeTestRule) {
    
    val settingsFab: SemanticsNodeInteraction
        get() = composeTestRule.onNodeWithContentDescription("Settings")
    
    fun clickSettingsFab() {
        settingsFab.performClick()
    }
    
    fun verifySettingsFabIsDisplayed() {
        settingsFab.assertIsDisplayed()
    }
    
    fun verifySettingsFabIsClickable() {
        settingsFab.assertIsEnabled()
    }
}

