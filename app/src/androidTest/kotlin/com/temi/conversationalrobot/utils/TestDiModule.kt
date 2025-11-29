package com.temi.conversationalrobot.utils

import com.temi.conversationalrobot.domain.repository.SettingsRepository
import com.temi.conversationalrobot.domain.services.*
import com.temi.conversationalrobot.data.service.WakeWordDetector
import com.temi.conversationalrobot.domain.usecase.ActivateConversationUseCase
import com.temi.conversationalrobot.domain.usecase.ActivateConversationUseCaseImpl
import com.temi.conversationalrobot.domain.usecase.HandleConversationUseCase
import com.temi.conversationalrobot.domain.usecase.HandleConversationUseCaseImpl
import com.temi.conversationalrobot.domain.usecase.HandleInterruptionUseCase
import com.temi.conversationalrobot.domain.usecase.HandleInterruptionUseCaseImpl
import com.temi.conversationalrobot.presentation.settings.SettingsViewModel
import com.temi.conversationalrobot.presentation.status.StatusIndicatorViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class MockInterruptionHandler : InterruptionHandler {
    private val _interruptionEvents = MutableSharedFlow<InterruptionEvent>(replay = 1)
    override val interruptionEvents: Flow<InterruptionEvent> = _interruptionEvents.asSharedFlow()
    
    override fun startMonitoring() {}
    override fun stopMonitoring() {}
    
    fun emitInterruption() {
        runBlocking {
            _interruptionEvents.emit(InterruptionEvent.UserSpeechDetected)
        }
    }
}

val testDataModule = module {
    single<SettingsRepository> { MockSettingsRepository() }
    single<SttService> { MockSttService() }
    single<LlmService> { MockLlmService() }
    single<TtsService> { MockTtsService() }
    single<WakeWordDetector> { MockWakeWordDetector() }
    single<InterruptionHandler> { MockInterruptionHandler() }
}

val testDomainModule = module {
    single<HandleConversationUseCase> { HandleConversationUseCaseImpl(get(), get(), get()) }
    single<ActivateConversationUseCase> { ActivateConversationUseCaseImpl(get()) }
    single<HandleInterruptionUseCase> { 
        HandleInterruptionUseCaseImpl(get(), get(), get(), get<HandleConversationUseCase>().conversationState) 
    }
}

val testPresentationModule = module {
    viewModel { StatusIndicatorViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
}

val testModules = listOf(
    testDataModule,
    testDomainModule,
    testPresentationModule
)

