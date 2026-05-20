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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val addedTrackDbConvertor: AddedTrackDbConvertor
): PlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(convertFromPlaylist(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists()
            .map { playlists -> convertFromPlaylistEntity(playlists) }
    }

    override fun getAddedTracks(idTracks: List<Int>): Flow<List<Track>> {
        return appDatabase.playlistDao()
            .getAddedTracks()
            .map { tracks ->
                tracks
                    .filter { trackEntity -> idTracks.contains(trackEntity.id) }
                    .map { trackEntity -> addedTrackDbConvertor.map(trackEntity) }
            }
    }

    override suspend fun insertNewTrackInPlaylist(playlistId: Int, track: Track): Boolean {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
        val playlist = playlistDbConvertor.map(playlistEntity)

        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            countTracks = updatedTrackIds.size
        )

        appDatabase.withTransaction {
            appDatabase.playlistDao().updatePlaylist(convertFromPlaylist(updatedPlaylist))
            appDatabase.playlistDao().insertTrackInAddedTracks(convertFromTrack(track))
        }

        return true
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return convertFromPlaylistEntity(appDatabase.playlistDao().getPlaylistById(id))
    }

    override suspend fun deleteTrack(trackId: Int, playlistId: Int) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
        val playlist = playlistDbConvertor.map(playlistEntity)
        val updatedIds = playlist.trackIds.filter { it != trackId }
        val updatedPlaylist = playlist.copy(trackIds = updatedIds, countTracks = updatedIds.size)
        appDatabase.playlistDao().updatePlaylist(convertFromPlaylist(updatedPlaylist))

        if (!isTrackInAnyPlaylist(trackId)) {
            appDatabase.playlistDao().deleteAddedTrack(trackId)
        }
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
        val playlist = playlistDbConvertor.map(playlistEntity)
        appDatabase.playlistDao().deletePlaylist(playlistId)
        playlist.trackIds.forEach { trackId ->
            if (!isTrackInAnyPlaylist(trackId)) {
                appDatabase.playlistDao().deleteAddedTrack(trackId)
            }
        }
    }

    private suspend fun isTrackInAnyPlaylist(trackId: Int): Boolean {
        return appDatabase.playlistDao().getAllPlaylists().any { entity ->
            playlistDbConvertor.map(entity).trackIds.contains(trackId)
        }
    }

    private fun convertFromTrack(track: Track): AddedTracksEntity {
        return addedTrackDbConvertor.map(track)
    }

    private fun convertFromPlaylist(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertFromPlaylistEntity(playlist: PlaylistEntity): Playlist {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }
}
