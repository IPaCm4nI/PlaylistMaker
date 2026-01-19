package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.App.Companion.THEME_KEY
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val sharedPreferences: SharedPreferences) : ThemeRepository {
    override fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun setTheme(isDark: Boolean) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, isDark)
            .apply()
    }
}
