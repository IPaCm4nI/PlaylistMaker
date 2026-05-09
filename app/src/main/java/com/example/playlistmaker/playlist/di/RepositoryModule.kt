package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.playlist.domain.db.api.PlaylistRepository
import org.koin.dsl.module

val playlistRepository = module {
    factory { PlaylistDbConvertor() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }
}