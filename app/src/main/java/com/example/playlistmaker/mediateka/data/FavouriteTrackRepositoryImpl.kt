package com.example.playlistmaker.mediateka.data

import com.example.playlistmaker.mediateka.data.converters.TrackDbConvertor
import com.example.playlistmaker.mediateka.data.db.AppDatabase
import com.example.playlistmaker.mediateka.data.db.entity.TrackEntity
import com.example.playlistmaker.mediateka.domain.db.api.FavouriteTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavouriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
): FavouriteTrackRepository {
    override suspend fun insertTrack(track: Track) {
        val trackWithTimestamp = track.copy(
            addedAt = System.currentTimeMillis(),
            isFavourite = true
            )
        appDatabase.trackDao().insertTrack(convertFromTrack(trackWithTimestamp))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().deleteTrack(convertFromTrack(track))
    }

    override fun getTracks(): Flow<List<Track>> {
        return appDatabase.trackDao().getTracks()
            .map { tracks -> convertFromTrackEntity(tracks) }
    }

    private fun convertFromTrack(track: Track): TrackEntity {
        return trackDbConvertor.map(track)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track ->  trackDbConvertor.map(track) }
    }
}