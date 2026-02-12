package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.SongRepositoryImpl
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.SongApi
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.api.SongInteractor
import com.example.playlistmaker.search.domain.api.SongRepository
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.search.domain.impl.HistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.SongInteractorImpl
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.search.data.storage.PrefsSearchStorageClient
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.settings.data.storage.PrefsSettingsStorageClient
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.ResourceProviderImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.ResourceProvider
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.google.gson.reflect.TypeToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    lateinit var application: Application

    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(
            PrefsSettingsStorageClient(
                context,
                "KEY_THEME"
            )
        )
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    private fun getResourceProvider(context: Context): ResourceProvider {
        return ResourceProviderImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            getExternalNavigator(context),
            getResourceProvider(context)
        )
    }
}