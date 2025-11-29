package com.temi.conversationalrobot

import android.app.Application
import android.util.Log
import com.temi.conversationalrobot.data.service.WakeWordDetector
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.domain.services.LlmService
import com.temi.conversationalrobot.domain.usecase.HandleInterruptionUseCase
import com.temi.conversationalrobot.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ConversationalRobotApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "Initializing ConversationalRobotApplication")
        
        startKoin {
            androidContext(this@ConversationalRobotApplication)
            modules(AppModule.modules)
        }
        
        // Initialize services at app startup
        applicationScope.launch {
            try {
                // Initialize LLM model
                val llmService: LlmService by inject()
                Log.d(TAG, "Initializing LLM service...")
                llmService.initialize()
                
                // Initialize interruption handler
                val interruptionHandler: HandleInterruptionUseCase by inject()
                interruptionHandler.initialize()
                
                // Start wake word detection if enabled
                val wakeWordDetector: WakeWordDetector by inject()
                val settingsRepository: SettingsRepository by inject()
                val settings = settingsRepository.getSettings().first()
                
                if (settings.wakeWordEnabled) {
                    Log.d(TAG, "Starting wake word detection...")
                    wakeWordDetector.startListening()
                }
                
                Log.d(TAG, "Application initialization complete")
            } catch (e: Exception) {
                Log.e(TAG, "Error during initialization: ${e.message}", e)
            }
        }
    }
    
    companion object {
        private const val TAG = "ConversationalRobotApp"
    }
}

