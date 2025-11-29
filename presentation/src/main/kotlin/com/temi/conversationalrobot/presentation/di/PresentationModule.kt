package com.temi.conversationalrobot.presentation.di

import com.temi.conversationalrobot.presentation.settings.SettingsViewModel
import com.temi.conversationalrobot.presentation.status.StatusIndicatorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { StatusIndicatorViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
}

