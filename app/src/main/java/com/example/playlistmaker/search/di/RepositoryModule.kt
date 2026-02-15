package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.data.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.SongRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.api.SongRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<SongRepository> {
        SongRepositoryImpl(get())
    }

    factory<HistoryRepository> {
        HistoryRepositoryImpl(get())
    }
}