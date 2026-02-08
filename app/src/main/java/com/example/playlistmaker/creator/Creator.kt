package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.SongRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.SongApi
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.api.SongInteractor
import com.example.playlistmaker.search.domain.api.SongRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.SongInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.search.ui.activity.SearchActivity
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

    private fun getSongsRepository(context: Context): SongRepository {
        return SongRepositoryImpl(RetrofitNetworkClient(getSongApi(), context))
    }

    fun provideSongsInteractor(context: Context): SongInteractor {
        return SongInteractorImpl(getSongsRepository(context))
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(
            application.getSharedPreferences(
                SearchActivity.Companion.FILE_HISTORY_TRACK,
                Context.MODE_PRIVATE
            )
        )
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    private fun getThemeRepository(): ThemeRepository {
        return ThemeRepositoryImpl(
            application.getSharedPreferences(App.Companion.PREFERENCES_FILE, Context.MODE_PRIVATE)
        )
    }

    fun provideThemeInteractor(): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }
}