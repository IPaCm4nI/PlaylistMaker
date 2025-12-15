package com.example.playlistmaker

import android.os.Parcelable
import java.util.UUID

data class Track(
    val id: String = UUID.randomUUID().toString(),
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?
) {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg")
}