package com.temi.conversationalrobot.di

import com.temi.conversationalrobot.data.di.dataModule
import com.temi.conversationalrobot.domain.di.domainModule
import com.temi.conversationalrobot.presentation.di.presentationModule
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModule {
    val modules: List<Module> = listOf(
        dataModule,
        domainModule,
        presentationModule
    )
}

