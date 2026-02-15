package com.example.playlistmaker.search.di

import android.content.Context
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.StorageClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.SongApi
import com.example.playlistmaker.search.data.storage.PrefsSearchStorageClient
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    factory { Gson() }

    single<SongApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SongApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("FILE_HISTORY_TRACK", Context.MODE_PRIVATE)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<StorageClient<ArrayList<Track>>> {
        PrefsSearchStorageClient(
            get(),
            get(),
            "KEY_HISTORY_TRACK",
            object: TypeToken<ArrayList<Track>>() {}.type
        )
    }
}