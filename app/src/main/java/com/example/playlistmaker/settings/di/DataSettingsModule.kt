package com.example.playlistmaker.settings.di

import android.content.Context
import com.example.playlistmaker.settings.data.StorageClient
import com.example.playlistmaker.settings.data.storage.PrefsSettingsStorageClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataSettingsModule = module {
    single {
        androidContext()
            .getSharedPreferences("PREFERENCES_FILE", Context.MODE_PRIVATE)
    }

    single<StorageClient> {
        PrefsSettingsStorageClient(
            "KEY_THEME",
            get()
        )
    }
}