package com.example.spanishwords.settings.domain.interactors

import com.example.spanishwords.settings.domain.api.SettingsInteractor
import com.example.spanishwords.settings.domain.repositories.SettingsRepository

class SettingsInteractorImpl(private val settings: SettingsRepository) :
    SettingsInteractor {

    override fun getThemeSettings(): Boolean {
        return settings.getThemeSettings()
    }

    override fun setThemeSetting(currentStatus: Boolean) {
        settings.setThemeSetting(currentStatus)
    }

}