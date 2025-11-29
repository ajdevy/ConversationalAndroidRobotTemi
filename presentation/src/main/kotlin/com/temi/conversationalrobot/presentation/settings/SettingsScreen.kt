package com.temi.conversationalrobot.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Manual Activation Button
            Button(
                onClick = { viewModel.activateConversation() },
                enabled = uiState.canActivate,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Conversation")
            }
            
            // Activation Section
            SettingsSection(title = "Activation") {
                SwitchRow(
                    label = "Wake Word",
                    checked = uiState.wakeWordEnabled,
                    onCheckedChange = { viewModel.updateWakeWordEnabled(it) }
                )
                
                Text("Wake Word Sensitivity: ${when(uiState.wakeWordSensitivity) {
                    0 -> "Low"
                    1 -> "Medium"
                    2 -> "High"
                    else -> "Medium"
                }}")
                
                Slider(
                    value = uiState.wakeWordSensitivity.toFloat(),
                    onValueChange = { viewModel.updateWakeWordSensitivity(it.toInt()) },
                    valueRange = 0f..2f,
                    steps = 1
                )
            }
            
            // Speech Section
            SettingsSection(title = "Speech") {
                Text("TTS Speech Rate: ${uiState.ttsSpeechRate.toInt()} WPM")
                Slider(
                    value = uiState.ttsSpeechRate,
                    onValueChange = { viewModel.updateTtsSpeechRate(it) },
                    valueRange = 50f..250f
                )
            }
            
            // Display Section
            SettingsSection(title = "Display") {
                Text("Status Indicator Size: ${(uiState.statusIndicatorSize * 100).toInt()}%")
                Slider(
                    value = uiState.statusIndicatorSize,
                    onValueChange = { viewModel.updateStatusIndicatorSize(it) },
                    valueRange = 0.4f..0.8f
                )
            }
            
            // Debug Section
            SettingsSection(title = "Debug") {
                SwitchRow(
                    label = "Display Debug Info",
                    checked = uiState.debugInfoEnabled,
                    onCheckedChange = { viewModel.updateDebugInfo(it) }
                )
                
                SwitchRow(
                    label = "Interruption Detection",
                    checked = uiState.interruptionDetectionEnabled,
                    onCheckedChange = { viewModel.updateInterruptionDetection(it) }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        content()
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

