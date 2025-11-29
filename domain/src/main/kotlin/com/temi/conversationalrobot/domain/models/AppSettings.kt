package com.temi.conversationalrobot.domain.models

data class AppSettings(
    val wakeWordEnabled: Boolean = true,
    val wakeWordSensitivity: Int = 1, // 0=Low, 1=Medium, 2=High
    val ttsSpeechRate: Float = 150f, // 50-250 WPM, default 150
    val debugInfoEnabled: Boolean = false,
    val statusIndicatorSize: Float = 0.6f, // 0.4-0.8, default 0.6 (60%)
    val interruptionDetectionEnabled: Boolean = true
)




