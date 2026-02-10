package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        Creator.application = this

        val themeInteractor = Creator.provideThemeInteractor(this)

        applyTheme(themeInteractor.getTheme())
    }

    companion object {
        fun applyTheme(isDark: Boolean) {
            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}