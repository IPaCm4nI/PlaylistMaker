package com.example.playlistmaker.search.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface SongsState{
    object Loading: SongsState

    data class Content(
        val songs: List<Track>,
        val isHistory: Boolean = false
    ) : SongsState

    data class Error(
        val errorMessage: String
    ) : SongsState

    data class Empty(
        val message: String
    ) : SongsState
}