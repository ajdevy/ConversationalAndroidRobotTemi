package com.temi.conversationalrobot.domain.di

import com.temi.conversationalrobot.domain.usecase.ActivateConversationUseCase
import com.temi.conversationalrobot.domain.usecase.ActivateConversationUseCaseImpl
import com.temi.conversationalrobot.domain.usecase.HandleConversationUseCase
import com.temi.conversationalrobot.domain.usecase.HandleConversationUseCaseImpl
import com.temi.conversationalrobot.domain.usecase.HandleInterruptionUseCase
import com.temi.conversationalrobot.domain.usecase.HandleInterruptionUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    single<HandleConversationUseCase> { HandleConversationUseCaseImpl(get(), get(), get()) }
    single<ActivateConversationUseCase> { ActivateConversationUseCaseImpl(get()) }
    single<HandleInterruptionUseCase> { 
        HandleInterruptionUseCaseImpl(get(), get(), get(), get<HandleConversationUseCase>().conversationState) 
    }
}

