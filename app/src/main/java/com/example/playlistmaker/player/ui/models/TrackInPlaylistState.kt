package com.example.playlistmaker.player.ui.models

sealed interface TrackInPlaylistState {
    data class Exists(
        val playlistName: String
    ) : TrackInPlaylistState

    data class NotExists(
        val playlistName: String
    ) : TrackInPlaylistState
}