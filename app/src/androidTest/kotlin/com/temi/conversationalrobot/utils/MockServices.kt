package com.temi.conversationalrobot.utils

import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.domain.services.*
import com.temi.conversationalrobot.data.service.WakeWordDetector
import com.temi.conversationalrobot.data.service.WakeWordDetectionEvent
import com.temi.conversationalrobot.domain.models.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

class MockSttService : SttService {
    private val _transcriptionResults = MutableSharedFlow<SttResult>(replay = 1)
    override val transcriptionResults: Flow<SttResult> = _transcriptionResults.asSharedFlow()
    
    private var _isListening = false
    
    override fun startListening() {
        _isListening = true
    }
    
    override fun stopListening() {
        _isListening = false
    }
    
    override fun isListening(): Boolean = _isListening
    
    // Test helper methods
    fun emitSuccess(text: String) {
        runBlocking {
            _transcriptionResults.emit(SttResult.Success(text))
        }
        android.util.Log.d("TestDebug", "MockSttService: emitSuccess('$text') - emitted")
    }
    
    fun emitError(message: String) {
        runBlocking {
            _transcriptionResults.emit(SttResult.Error(message))
        }
        android.util.Log.d("TestDebug", "MockSttService: emitError('$message') - emitted")
    }
    
    fun emitTimeout() {
        runBlocking {
            _transcriptionResults.emit(SttResult.Timeout)
        }
        android.util.Log.d("TestDebug", "MockSttService: emitTimeout() - emitted")
    }
}

class MockLlmService : LlmService {
    private val _responses = MutableSharedFlow<LlmResult>(replay = 1)
    override val responses: Flow<LlmResult> = _responses.asSharedFlow()
    
    private var pendingResponse: LlmResult? = null
    
    override suspend fun generateResponse(userUtterance: String) {
        // Emit pending response if set, otherwise emit default success
        val response = pendingResponse ?: LlmResult.Success("Mock response for: $userUtterance")
        _responses.emit(response)
        pendingResponse = null
    }
    
    override suspend fun initialize() {
        // Mock initialization - always succeeds
    }
    
    // Test helper methods
    fun emitSuccess(text: String) {
        pendingResponse = LlmResult.Success(text)
        android.util.Log.d("TestDebug", "MockLlmService: emitSuccess('$text') - pending response set")
    }
    
    fun emitError(message: String) {
        pendingResponse = LlmResult.Error(message)
        android.util.Log.d("TestDebug", "MockLlmService: emitError('$message') - pending response set")
    }
    
    fun emitTimeout() {
        pendingResponse = LlmResult.Timeout
        android.util.Log.d("TestDebug", "MockLlmService: emitTimeout() - pending response set")
    }
}

class MockTtsService : TtsService {
    private val _speakingState = MutableStateFlow<SpeakingState>(SpeakingState.Idle)
    override val speakingState: StateFlow<SpeakingState> = _speakingState.asStateFlow()
    
    private val _ttsEvents = MutableSharedFlow<TtsEvent>(replay = 1)
    override val ttsEvents: Flow<TtsEvent> = _ttsEvents.asSharedFlow()
    
    override fun speak(text: String) {
        _speakingState.value = SpeakingState.Speaking
        runBlocking {
            _ttsEvents.emit(TtsEvent.Started)
        }
        android.util.Log.d("TestDebug", "MockTtsService: speak('$text') - TtsEvent.Started emitted")
    }
    
    override fun stop() {
        _speakingState.value = SpeakingState.Idle
        android.util.Log.d("TestDebug", "MockTtsService: stop() called")
    }
    
    // Test helper methods
    fun emitCompleted() {
        _speakingState.value = SpeakingState.Idle
        runBlocking {
            _ttsEvents.emit(TtsEvent.Completed)
        }
        android.util.Log.d("TestDebug", "MockTtsService: emitCompleted() - emitted")
    }
    
    fun emitError(message: String) {
        _speakingState.value = SpeakingState.Idle
        runBlocking {
            _ttsEvents.emit(TtsEvent.Error(message))
        }
        android.util.Log.d("TestDebug", "MockTtsService: emitError('$message') - emitted")
    }
}

class MockWakeWordDetector : WakeWordDetector {
    private val _detectionEvents = MutableSharedFlow<WakeWordDetectionEvent>(replay = 1)
    override val detectionEvents: Flow<WakeWordDetectionEvent> = _detectionEvents.asSharedFlow()
    
    private var _isListening = false
    
    override fun startListening() {
        _isListening = true
    }
    
    override fun stopListening() {
        _isListening = false
    }
    
    override fun isListening(): Boolean = _isListening
    
    // Test helper methods
    fun emitDetection() {
        runBlocking {
            _detectionEvents.emit(WakeWordDetectionEvent.Detected)
        }
    }
    
    fun emitError(message: String) {
        runBlocking {
            _detectionEvents.emit(WakeWordDetectionEvent.Error(message))
        }
    }
}

class MockSettingsRepository : SettingsRepository {
    private val _settings = MutableStateFlow(AppSettings())
    override val settingsStateFlow: StateFlow<AppSettings> = _settings.asStateFlow()
    
    override fun getSettings(): Flow<AppSettings> = _settings
    
    override suspend fun updateWakeWordEnabled(enabled: Boolean) {
        _settings.value = _settings.value.copy(wakeWordEnabled = enabled)
    }
    
    override suspend fun updateWakeWordSensitivity(sensitivity: Int) {
        _settings.value = _settings.value.copy(wakeWordSensitivity = sensitivity)
    }
    
    override suspend fun updateTtsSpeechRate(rate: Float) {
        _settings.value = _settings.value.copy(ttsSpeechRate = rate)
    }
    
    override suspend fun updateDebugInfo(enabled: Boolean) {
        _settings.value = _settings.value.copy(debugInfoEnabled = enabled)
    }
    
    override suspend fun updateStatusIndicatorSize(size: Float) {
        _settings.value = _settings.value.copy(statusIndicatorSize = size)
    }
    
    override suspend fun updateInterruptionDetection(enabled: Boolean) {
        _settings.value = _settings.value.copy(interruptionDetectionEnabled = enabled)
    }
    
    // Test helper method
    fun setSettings(settings: AppSettings) {
        _settings.value = settings
    }
}

