package com.temi.conversationalrobot.presentation.status

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.temi.conversationalrobot.domain.models.ConversationState

@Composable
fun StatusIndicatorScreen(
    viewModel: StatusIndicatorViewModel,
    onSettingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val minDimension = minOf(screenWidth, screenHeight)
    val circleDiameter = minDimension * uiState.indicatorSize
    
    val color = when (uiState.currentState) {
        ConversationState.Idle -> Color(0xFF808080) // Grey
        ConversationState.Listening, ConversationState.Thinking -> Color(0xFFFF0000) // Red
        ConversationState.Speaking -> Color(0xFF00FF00) // Green
        is ConversationState.Error -> Color(0xFF808080)
    }
    
    val shouldAnimate = uiState.currentState != ConversationState.Idle
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Pulsating circle
        PulsatingCircle(
            color = color,
            diameter = circleDiameter,
            shouldAnimate = shouldAnimate
        )
        
        // Debug overlay
        if (uiState.debugInfoEnabled) {
            DebugOverlay(
                transcriptionText = uiState.transcriptionText,
                llmResponseText = uiState.llmResponseText
            )
        }
        
        // Settings FAB
        FloatingActionButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }
    }
}

@Composable
private fun PulsatingCircle(
    color: Color,
    diameter: androidx.compose.ui.unit.Dp,
    shouldAnimate: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsating")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "color"
    )
    
    Canvas(
        modifier = Modifier.size(diameter)
    ) {
        val radius = size.minDimension / 2f
        val currentScale = if (shouldAnimate) scale else 1.0f
        
        drawCircle(
            color = animatedColor,
            radius = radius * currentScale,
            center = Offset(size.width / 2f, size.height / 2f)
        )
    }
}

@Composable
private fun DebugOverlay(
    transcriptionText: String,
    llmResponseText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        if (transcriptionText.isNotEmpty()) {
            Text(
                text = "Transcription: $transcriptionText",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (llmResponseText.isNotEmpty()) {
            Text(
                text = "Response: $llmResponseText",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

