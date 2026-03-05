package com.example.playlistmaker.mediateka.di

import com.example.playlistmaker.mediateka.ui.view_model.FavouriteViewModel
import com.example.playlistmaker.mediateka.ui.view_model.PlaylistsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelMediatekaModule = module {

    viewModel {
        FavouriteViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}