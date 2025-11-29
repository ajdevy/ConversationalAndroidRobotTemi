package com.temi.conversationalrobot.data.di

import com.temi.conversationalrobot.data.ToonDataLoader
import com.temi.conversationalrobot.data.repository.SettingsRepositoryImpl
import com.temi.conversationalrobot.data.repository.ToonRepository
import com.temi.conversationalrobot.data.repository.ToonRepositoryImpl
import com.temi.conversationalrobot.data.service.InterruptionHandlerImpl
import com.temi.conversationalrobot.data.service.LlmServiceImpl
import com.temi.conversationalrobot.data.service.SttServiceImpl
import com.temi.conversationalrobot.data.service.TtsServiceImpl
import com.temi.conversationalrobot.data.service.WakeWordDetector
import com.temi.conversationalrobot.data.service.WakeWordDetectorImpl
import com.temi.conversationalrobot.domain.services.InterruptionHandler
import com.temi.conversationalrobot.domain.services.LlmService
import com.temi.conversationalrobot.domain.services.SttService
import com.temi.conversationalrobot.domain.services.TtsService
import com.temi.conversationalrobot.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<ToonDataLoader> { ToonDataLoader(androidContext()) }
    single<ToonRepository> { ToonRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }
    single<WakeWordDetector> { WakeWordDetectorImpl(androidContext(), get()) }
    single<SttService> { SttServiceImpl(androidContext()) }
    single<LlmService> { LlmServiceImpl(androidContext(), get()) }
    single<TtsService> { TtsServiceImpl(androidContext(), get()) }
    single<InterruptionHandler> { InterruptionHandlerImpl(get(), get()) }
}

