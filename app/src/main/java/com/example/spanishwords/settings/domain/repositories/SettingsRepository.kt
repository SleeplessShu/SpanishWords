package com.example.spanishwords.settings.domain.repositories

interface SettingsRepository {
    fun getThemeSettings(): Boolean
    fun setThemeSetting(currentStatus: Boolean)
}