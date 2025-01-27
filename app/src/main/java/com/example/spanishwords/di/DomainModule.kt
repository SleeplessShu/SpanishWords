package com.example.spanishwords.di
import com.example.spanishwords.game.domain.api.DatabaseInteractor
import com.example.spanishwords.game.domain.api.GameInteractor
import com.example.spanishwords.game.domain.api.ScoreInteractor
import com.example.spanishwords.game.domain.interactors.DatabaseInteractorImpl
import com.example.spanishwords.game.domain.interactors.GameInteractorImpl
import com.example.spanishwords.game.domain.interactors.ScoreInteractorImpl
import com.example.spanishwords.settings.domain.api.SettingsInteractor
import com.example.spanishwords.settings.domain.api.SharingInteractor
import com.example.spanishwords.settings.domain.interactors.SettingsInteractorImpl
import com.example.spanishwords.settings.domain.interactors.SharingInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single <DatabaseInteractor>{
        DatabaseInteractorImpl(get())
    }

    single <GameInteractor>{
       GameInteractorImpl(get())
    }

    single <ScoreInteractor> {
        ScoreInteractorImpl(get())
    }

}