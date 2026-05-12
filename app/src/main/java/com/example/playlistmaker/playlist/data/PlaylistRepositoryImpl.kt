package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.mediateka.data.db.AppDatabase
import com.example.playlistmaker.playlist.data.converters.AddedTrackDbConvertor
import com.example.playlistmaker.playlist.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.playlist.data.db.entity.AddedTracksEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.db.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import androidx.room.withTransaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val addedTrackDbConvertor: AddedTrackDbConvertor,
    private val gson: Gson
): PlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(convertFromPlaylist(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists()
            .map { playlists -> convertFromPlaylistEntity(playlists) }
    }

    override suspend fun insertNewTrackInPlaylist(playlist: Playlist, track: Track): Boolean {
        val currentTrackIds = getTrackIdsFromPlaylist(playlist).toMutableList()

        currentTrackIds.add(track.trackId)

        val updatedPlaylist = playlist.copy(
            tracksInPlaylist = gson.toJson(currentTrackIds),
            countTracks = currentTrackIds.size
        )

        appDatabase.withTransaction {
            appDatabase.playlistDao().updatePlaylist(
                convertFromPlaylist(updatedPlaylist)
            )

            appDatabase.playlistDao().insertTrackInAddedTracks(
                convertFromTrack(track)
            )
        }

        return true
    }

    private fun getTrackIdsFromPlaylist(playlist: Playlist): List<Int> {
        if (playlist.tracksInPlaylist.isBlank()) return emptyList()
        val type = object : TypeToken<List<Int>>() {}.type

        return gson.fromJson(playlist.tracksInPlaylist, type) ?: emptyList()
    }

    private fun convertFromTrack(track: Track): AddedTracksEntity {
        return addedTrackDbConvertor.map(track)
    }

    private fun convertFromPlaylist(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist ->  playlistDbConvertor.map(playlist) }
    }
}