package com.example.playlistmaker.mediateka.di

import com.example.playlistmaker.mediateka.data.FavouriteTrackRepositoryImpl
import com.example.playlistmaker.mediateka.data.converters.TrackDbConvertor
import com.example.playlistmaker.mediateka.domain.db.api.FavouriteTrackRepository
import org.koin.dsl.module

val repositoryMediaModule = module {
    factory { TrackDbConvertor() }

    single<FavouriteTrackRepository> {
        FavouriteTrackRepositoryImpl(get(), get())
    }
}