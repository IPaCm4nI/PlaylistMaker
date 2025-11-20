package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES

const val PREFERENCES_FILE = "preferences_file"
const val THEME_KEY = "theme_key"

class App: Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)

        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val sharedPrefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)
        sharedPrefs
            .edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()

        applyTheme(darkTheme)
    }

    private fun applyTheme(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                MODE_NIGHT_YES
            } else {
                MODE_NIGHT_NO
            }
        )
    }
}