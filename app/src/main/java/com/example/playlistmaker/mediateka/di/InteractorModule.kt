package com.example.playlistmaker.mediateka.di

import com.example.playlistmaker.mediateka.domain.db.api.FavouriteTrackInteractor
import com.example.playlistmaker.mediateka.domain.db.impl.FavouriteTrackInteractorImpl
import org.koin.dsl.module

val interactorMediaModule = module {
    single<FavouriteTrackInteractor> {
        FavouriteTrackInteractorImpl(get())
    }
}