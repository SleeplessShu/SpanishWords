package com.example.spanishwords.di

import android.content.Context
import android.os.Handler
import androidx.room.Room
import com.example.spanishwords.App
import com.example.spanishwords.game.data.database.AppDatabase
import com.example.spanishwords.game.data.repositories.DatabaseRepositoryImpl
import com.example.spanishwords.game.domain.repositories.DatabaseRepository
import com.example.spanishwords.settings.data.ExternalNavigatorRepositoryImpl
import com.example.spanishwords.settings.data.SettingsRepositoryImpl
import com.example.spanishwords.settings.data.SharingRepositoryImpl
import com.example.spanishwords.settings.domain.repositories.ExternalNavigatorRepository
import com.example.spanishwords.settings.domain.repositories.SettingsRepository
import com.example.spanishwords.settings.domain.repositories.SharingRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {

    single(named("themePreferences")) {
        App.appContext.getSharedPreferences("NightMode", Context.MODE_PRIVATE)
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
    single<DatabaseRepository> {
        DatabaseRepositoryImpl(get())
    }
    factory<Handler> {
        Handler()
    }
    single<Context> {
        App.appContext
    }
    single { get<AppDatabase>().wordDao() }

    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "dictionary.db"
        ).createFromAsset("databases/dictionary_new.db").build()
    }

}