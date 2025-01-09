package com.example.spanishwords.di

import android.os.Handler
import android.os.Looper
import com.example.spanishwords.settings.presentation.SettingsViewModel
import com.example.spanishwords.game.presentation.GameViewModel
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
        GameViewModel()
    }
}