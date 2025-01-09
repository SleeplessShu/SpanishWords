package com.example.spanishwords.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.spanishwords.settings.domain.repositories.SettingsRepository

class SettingsRepositoryImpl(
    private var sharedPreferences: SharedPreferences
) : SettingsRepository {

    override fun getThemeSettings(): Boolean {
        return sharedPreferences.getBoolean(THEME_STATUS_SHARED_PREFERENCES_KEY, false)
    }

    override fun setThemeSetting(currentStatus: Boolean) {
        sharedPreferences.edit {
            putBoolean(THEME_STATUS_SHARED_PREFERENCES_KEY, currentStatus)
        }
        switchTheme()
    }

    private fun switchTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (getThemeSettings()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        const val THEME_STATUS_SHARED_PREFERENCES_KEY: String = "NightMode"
    }
}