package com.temi.conversationalrobot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.temi.conversationalrobot.presentation.settings.SettingsScreen
import com.temi.conversationalrobot.presentation.settings.SettingsViewModel
import com.temi.conversationalrobot.presentation.status.StatusIndicatorScreen
import com.temi.conversationalrobot.presentation.status.StatusIndicatorViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    
    private val statusIndicatorViewModel: StatusIndicatorViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSettings by remember { mutableStateOf(false) }
                    
                    if (showSettings) {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onBackClick = { showSettings = false }
                        )
                    } else {
                        StatusIndicatorScreen(
                            viewModel = statusIndicatorViewModel,
                            onSettingsClick = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}

