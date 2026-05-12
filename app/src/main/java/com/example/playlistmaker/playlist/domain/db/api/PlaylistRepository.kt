package com.example.playlistmaker.playlist.domain.db.api

import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun insertNewTrackInPlaylist(playlist: Playlist, track: Track): Boolean
}