package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import org.koin.dsl.module

val repositorySettingsModule = module {
    factory<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }
}