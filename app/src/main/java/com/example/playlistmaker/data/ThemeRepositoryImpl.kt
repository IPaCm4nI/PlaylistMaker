package com.example.playlistmaker.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.example.playlistmaker.App.Companion.THEME_KEY
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : ThemeRepository {
    override fun getTheme(): Boolean {
        val isDark = sharedPreferences.getBoolean(THEME_KEY, false)
        applyTheme(isDark)
        return isDark
    }

    override fun setTheme(isDark: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, isDark)
            .apply()

        applyTheme(isDark)
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
