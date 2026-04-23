package com.example.playlistmaker.mediateka.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed class FavouriteState {
    object Empty: FavouriteState()

    data class Content(
        val favouriteTracks: List<Track>
    ): FavouriteState()
}