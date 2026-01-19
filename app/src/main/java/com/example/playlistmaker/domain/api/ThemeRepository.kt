package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun getTheme(): Boolean
    fun setTheme(isDark: Boolean)
}
