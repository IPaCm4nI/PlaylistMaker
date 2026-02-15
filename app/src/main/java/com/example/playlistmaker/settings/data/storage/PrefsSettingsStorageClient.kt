package com.example.playlistmaker.settings.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.StorageClient

class PrefsSettingsStorageClient(
        private val dataKey: String,
        private val prefs: SharedPreferences,
    ): StorageClient {

    override fun storeData(isTheme: Boolean) {
        prefs
            .edit()
            .putBoolean(dataKey, isTheme)
            .apply()
    }

    override fun getData(): Boolean {
        return prefs.getBoolean(dataKey, false)
    }
}