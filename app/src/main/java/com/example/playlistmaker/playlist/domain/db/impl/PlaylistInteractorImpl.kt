package com.example.playlistmaker.playlist.domain.db.impl

import com.example.playlistmaker.playlist.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.db.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
): PlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun insertNewTrackInPlaylist(playlist: Playlist, track: Track): Boolean {
        return playlistRepository.insertNewTrackInPlaylist(playlist, track)
    }
}