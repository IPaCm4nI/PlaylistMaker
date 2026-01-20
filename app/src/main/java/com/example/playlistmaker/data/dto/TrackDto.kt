package com.example.playlistmaker.data.dto

import java.util.UUID

data class TrackDto(
    val id: String = UUID.randomUUID().toString(),
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String
)
