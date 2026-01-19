package com.example.playlistmaker

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.HistoryRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.SongRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SongApi
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    lateinit var application: Application
    fun getSongApi(): SongApi {
        return Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SongApi::class.java)
    }

    private fun getSongsRepository(): SongRepository {
        return SongRepositoryImpl(RetrofitNetworkClient(getSongApi()))
    }

    fun provideSongsInteractor(): SongInteractor {
        return SongInteractorImpl(getSongsRepository())
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(
            application.getSharedPreferences(FILE_HISTORY_TRACK, MODE_PRIVATE)
        )
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    private fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(
            application.getSharedPreferences(App.PREFERENCES_FILE, MODE_PRIVATE)
        )
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }
}