package com.example.playlistmaker.settings.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.StorageClient

class PrefsSettingsStorageClient(
        private val context: Context,
        private val dataKey: String): StorageClient {

    private val prefs: SharedPreferences = context.getSharedPreferences("PREFERENCES_FILE", Context.MODE_PRIVATE)

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