package com.example.playlistmaker

import java.util.UUID

data class Track(
    val id: String = UUID.randomUUID().toString(),
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)