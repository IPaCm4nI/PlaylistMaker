package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES


class App: Application() {
    companion object {
        const val PREFERENCES_FILE = "preferences_file"
        const val THEME_KEY = "theme_key"
    }
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)
        val darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)

        applyTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
//        sharedPrefs
//            .edit()
//            .putBoolean(THEME_KEY, darkThemeEnabled)
//            .apply()

        applyTheme(darkThemeEnabled)
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