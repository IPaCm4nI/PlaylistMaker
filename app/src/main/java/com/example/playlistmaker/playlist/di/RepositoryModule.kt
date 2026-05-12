package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.data.converters.AddedTrackDbConvertor
import com.example.playlistmaker.playlist.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.playlist.domain.db.api.PlaylistRepository
import org.koin.dsl.module

val playlistRepository = module {
    factory { PlaylistDbConvertor() }

    factory { AddedTrackDbConvertor() }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get(), get())
    }
}