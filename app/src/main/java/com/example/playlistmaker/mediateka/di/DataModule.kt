package com.example.playlistmaker.mediateka.di

import androidx.room.Room
import com.example.playlistmaker.mediateka.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataMediaModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }
}