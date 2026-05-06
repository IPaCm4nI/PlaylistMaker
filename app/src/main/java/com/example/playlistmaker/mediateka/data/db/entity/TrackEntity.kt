package com.example.playlistmaker.mediateka.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_table")
data class TrackEntity(
    @PrimaryKey
    val id: Int,
    val artworkUrl100: String?,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: String,
    val previewUrl: String,
    val addedAt: Long,
    val isFavourite: Boolean
)