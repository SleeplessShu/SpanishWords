package com.example.spanishwords.di

import android.content.Context
import android.os.Handler
import com.example.spanishwords.App
import com.example.spanishwords.settings.data.ExternalNavigatorRepositoryImpl
import com.example.spanishwords.settings.data.SettingsRepositoryImpl
import com.example.spanishwords.settings.data.SharingRepositoryImpl
import com.example.spanishwords.settings.domain.repositories.ExternalNavigatorRepository
import com.example.spanishwords.settings.domain.repositories.SettingsRepository
import com.example.spanishwords.settings.domain.repositories.SharingRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val  dataModule = module {

    single(named("themePreferences")) {
        App.appContext
            .getSharedPreferences("NightMode", Context.MODE_PRIVATE)
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("themePreferences")))
    }
    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }
    single<ExternalNavigatorRepository> {
        ExternalNavigatorRepositoryImpl(get())
    }
    factory<Handler> {
        Handler()
    }
    single<Context> { App.appContext }
}