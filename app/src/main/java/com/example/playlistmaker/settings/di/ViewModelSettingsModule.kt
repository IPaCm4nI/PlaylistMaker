package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelSettingsModule = module {
    viewModel {
        SettingsViewModel(get(), get())
    }
}