package com.example.playlistmaker.mediateka.domain.db.impl

import com.example.playlistmaker.mediateka.domain.db.api.FavouriteTrackInteractor
import com.example.playlistmaker.mediateka.domain.db.api.FavouriteTrackRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteTrackInteractorImpl(
    private val favouriteTrackRepository: FavouriteTrackRepository
): FavouriteTrackInteractor {
    override suspend fun insertTrack(track: Track) {
        favouriteTrackRepository.insertTrack(track)
    }

    override suspend fun deleteTrack(trackId: Int) {
        favouriteTrackRepository.deleteTrack(trackId)
    }

    override fun getTracks(): Flow<List<Track>> {
        return favouriteTrackRepository.getTracks()
            .map{ tracks ->
                tracks.sortedByDescending { it.addedAt }
            }
    }
}