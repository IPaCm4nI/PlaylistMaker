package com.example.playlistmaker.playlist.domain.db.api

import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>
}