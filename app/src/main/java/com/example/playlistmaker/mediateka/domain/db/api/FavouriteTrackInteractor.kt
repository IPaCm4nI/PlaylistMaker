package com.example.playlistmaker.mediateka.domain.db.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTrackInteractor {
    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(trackId: Int)

    fun getTracks(): Flow<List<Track>>
}