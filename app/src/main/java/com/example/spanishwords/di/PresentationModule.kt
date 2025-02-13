package com.example.spanishwords.di

import android.os.Handler
import android.os.Looper
import com.example.spanishwords.settings.presentation.SettingsViewModel
import com.example.spanishwords.game.presentation.GameViewModel
import com.example.spanishwords.score.presentation.ScoreViewModel
import com.example.spanishwords.utils.SupportFunctions
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    factory<Handler> {
        Handler(Looper.getMainLooper())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        GameViewModel(get(), get(), get())
    }

    viewModel {
        ScoreViewModel(get(), get())
    }

    single { SupportFunctions() }
}