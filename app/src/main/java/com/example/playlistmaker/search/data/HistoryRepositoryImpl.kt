package com.example.playlistmaker.search.data

import com.example.playlistmaker.mediateka.data.db.AppDatabase
import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class HistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>,
    private val appDatabase: AppDatabase
): HistoryRepository {

    override fun saveToHistory(track: Track) {
        val songs = storage.getData()?.toMutableList() ?: mutableListOf()
        songs.removeIf { it.trackId == track.trackId }
        songs.add(0, track)
        val limitedSongs = ArrayList(songs.take(10))

        storage.storeData(limitedSongs)
    }

    override suspend fun getHistory(): Resource<List<Track>> {
        val favouriteIds = appDatabase.trackDao().getIdTrack().toSet()
        val songs = storage.getData() ?: listOf()

        val updatedSongs = songs.map { track ->
            track.copy(isFavourite = track.trackId in favouriteIds)
        }

        return Resource.Success(updatedSongs)
    }

    override fun clearHistory() {
        storage.clearData()
    }
}