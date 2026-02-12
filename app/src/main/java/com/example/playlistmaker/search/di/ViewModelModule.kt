package com.example.playlistmaker.search.di

import com.example.playlistmaker.search.ui.view_model.SongsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SongsViewModel(get(), get())
    }
}