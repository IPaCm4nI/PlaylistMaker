package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.SongInteractor
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.SongInteractorImpl
import org.koin.dsl.module
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val interactorModule = module {

    factory<HistoryInteractor> {
        HistoryInteractorImpl(get())
    }

    factory<SongInteractor> {
        SongInteractorImpl(get(), get())
    }

    single<Executor> {
        Executors.newCachedThreadPool()
    }
}