package com.example.spanishwords.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spanishwords.settings.domain.api.SettingsInteractor
import com.example.spanishwords.settings.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {
    private val _state = MutableLiveData<ThemeViewState>()
    val state: LiveData<ThemeViewState> get() = _state
    fun getThemeState(): Boolean {
        return settingsInteractor.getThemeSettings()
    }

    init {
        initializeTheme()
    }

    private fun initializeTheme() {
        val isNightModeOn = settingsInteractor.getThemeSettings()
        _state.value = ThemeViewState(isNightModeOn = isNightModeOn)
    }

    fun setTheme(isChecked: Boolean) {
        settingsInteractor.setThemeSetting(isChecked)
        initializeTheme()
    }

    fun supportSend() {
        sharingInteractor.openSupport()
    }

    fun openTerm() {
        sharingInteractor.openTerms()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }
}