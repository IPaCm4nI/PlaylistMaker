package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.db.impl.PlaylistInteractorImpl
import org.koin.dsl.module

val playlistInteractorModule = module {
    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}