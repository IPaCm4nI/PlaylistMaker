package com.example.playlistmaker.playlist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namePlaylist: String,
    val descriptionPlaylist: String?,
    val pathToImage: String?,
    val tracksInPlaylist: String,
    val countTracks: Int
)
