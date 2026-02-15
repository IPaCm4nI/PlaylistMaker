package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.api.ThemeRepository

class ThemeRepositoryImpl(
    private val storage: StorageClient
) : ThemeRepository {

    override fun getTheme(): Boolean {
        return storage.getData()
    }

    override fun setTheme(isDark: Boolean) {
        storage.storeData(isDark)
    }
}