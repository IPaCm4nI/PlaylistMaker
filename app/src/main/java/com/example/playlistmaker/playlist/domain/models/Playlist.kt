package com.example.playlistmaker.playlist.domain.models

data class Playlist(
    val id: Int = 0,
    val namePlaylist: String,
    val descriptionPlaylist: String?,
    val pathToImage: String?,
    val tracksInPlaylist: String = "",
    val countTracks: Int = 0
)