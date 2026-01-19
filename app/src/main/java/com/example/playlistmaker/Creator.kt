package com.example.playlistmaker

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.HistoryRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.SongRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.SongInteractor
import com.example.playlistmaker.domain.api.SongRepository
import com.example.playlistmaker.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.SongInteractorImpl
import com.example.playlistmaker.ui.search.SearchActivity.Companion.FILE_HISTORY_TRACK

object Creator {
    private fun getSongsRepository(): SongRepository {
        return SongRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSongsInteractor(): SongInteractor {
        return SongInteractorImpl(getSongsRepository())
    }

    private fun getHistoryRepository(application: Application): HistoryRepository {
        return HistoryRepositoryImpl(application.getSharedPreferences(FILE_HISTORY_TRACK, MODE_PRIVATE))
    }

    fun provideHistoryInteractor(application: Application): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository(application))
    }

    private fun getThemeRepository(application: Application): ThemeRepository {
        return ThemeRepositoryImpl(application.getSharedPreferences(App.PREFERENCES_FILE, MODE_PRIVATE))
    }

    fun provideThemeInteractor(application: Application): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(application))
    }
}