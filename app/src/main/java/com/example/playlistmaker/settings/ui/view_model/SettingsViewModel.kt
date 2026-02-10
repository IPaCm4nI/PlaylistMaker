package com.example.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor
): ViewModel() {
    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as Application)
                val sharingInteractor = Creator.provideSharingInteractor(app)
                val themeInteractor = Creator.provideThemeInteractor(app)
                SettingsViewModel(sharingInteractor, themeInteractor)
            }
        }
    }

    private val isTheme = MutableLiveData(themeInteractor.getTheme())
    fun observerIsTheme(): LiveData<Boolean> = isTheme

    fun setTheme(isDark: Boolean) {
        themeInteractor.setTheme(isDark)
        App.applyTheme(isDark)
        isTheme.postValue(isDark)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}