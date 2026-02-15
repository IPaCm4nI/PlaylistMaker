package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Track(
    val id: String = UUID.randomUUID().toString(),
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String
): Parcelable {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg")
}