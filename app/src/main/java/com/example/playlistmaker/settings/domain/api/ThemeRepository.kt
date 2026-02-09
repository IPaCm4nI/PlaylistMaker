package com.example.playlistmaker.settings.domain.api

interface ThemeRepository {
    fun getTheme(): Boolean
    fun setTheme(isDark: Boolean)
}
