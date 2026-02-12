package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.di.dataModule
import com.example.playlistmaker.search.di.interactorModule
import com.example.playlistmaker.search.di.repositoryModule
import com.example.playlistmaker.search.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

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