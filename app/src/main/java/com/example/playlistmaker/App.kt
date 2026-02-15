package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.player.di.viewModelPlayerModule
import com.example.playlistmaker.search.di.dataModule
import com.example.playlistmaker.search.di.interactorModule
import com.example.playlistmaker.search.di.repositoryModule
import com.example.playlistmaker.search.di.viewModelSearchModule
import com.example.playlistmaker.settings.di.dataSettingsModule
import com.example.playlistmaker.settings.di.repositorySettingsModule
import com.example.playlistmaker.settings.di.themeInteractorModule
import com.example.playlistmaker.settings.di.viewModelSettingsModule
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.sharing.di.dataSharingModule
import com.example.playlistmaker.sharing.di.sharingInteractorModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class App: Application(), KoinComponent {
    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelSearchModule,
                viewModelPlayerModule, dataSettingsModule, repositorySettingsModule,
                themeInteractorModule, viewModelSettingsModule, dataSharingModule,
                sharingInteractorModule
            )
        }

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