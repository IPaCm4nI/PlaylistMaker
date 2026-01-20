package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl(private val settingsRepository: ThemeRepository) : ThemeInteractor {

    override fun getTheme(): Boolean {
        return settingsRepository.getTheme()
    }

    override fun setTheme(isDark: Boolean) {
        settingsRepository.setTheme(isDark)
    }
}
