package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelPlayerModule = module {
    viewModel {
        (url: String) -> PlayerViewModel(url, get())
    }

    factory {
        MediaPlayer()
    }
}