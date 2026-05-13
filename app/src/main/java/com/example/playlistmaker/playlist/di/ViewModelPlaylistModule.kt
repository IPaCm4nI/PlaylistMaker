package com.example.playlistmaker.playlist.di

import com.example.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelPlaylistModule = module {
    viewModel {
        PlaylistViewModel(get())
    }
}