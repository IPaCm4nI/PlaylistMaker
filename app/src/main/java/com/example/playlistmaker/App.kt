package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.creator.Creator


class App: Application() {

    lateinit var themeInteractor: ThemeInteractor
    companion object {
        const val PREFERENCES_FILE = "preferences_file"
        const val THEME_KEY = "theme_key"
    }

    override fun onCreate() {
        super.onCreate()

        Creator.application = this

        themeInteractor = Creator.provideThemeInteractor()

        themeInteractor.getTheme()
    }
}