package com.example.playlistmaker.playlist.domain.db.api

import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getAddedTracks(idTracks: List<Int>): Flow<List<Track>>

    suspend fun insertNewTrackInPlaylist(playlistId: Int, track: Track): Boolean

    suspend fun getPlaylistById(id: Int): Playlist

    suspend fun deleteTrack(trackId: Int, playlistId: Int)
}