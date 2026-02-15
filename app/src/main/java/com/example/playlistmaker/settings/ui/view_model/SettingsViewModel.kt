package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor
): ViewModel() {
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