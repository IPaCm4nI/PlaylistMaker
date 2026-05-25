package com.example.playlistmaker.playlist.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Int = 0,
    val namePlaylist: String,
    val descriptionPlaylist: String?,
    val pathToImage: String?,
    val trackIds: List<Int> = emptyList(),
    val countTracks: Int = 0
): Parcelable