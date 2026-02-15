package com.example.playlistmaker.sharing.di

import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.ResourceProviderImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.ResourceProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataSharingModule = module {
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<ResourceProvider> {
        ResourceProviderImpl(androidContext())
    }
}