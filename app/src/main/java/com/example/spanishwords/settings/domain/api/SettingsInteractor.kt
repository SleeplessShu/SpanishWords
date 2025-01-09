package com.example.spanishwords.settings.domain.api

interface SettingsInteractor {
    fun getThemeSettings(): Boolean
    fun setThemeSetting(currentStatus: Boolean)
}
