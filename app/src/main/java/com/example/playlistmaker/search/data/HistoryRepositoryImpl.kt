package com.example.playlistmaker.search.data

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class HistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>): HistoryRepository {

    override fun saveToHistory(track: Track) {
        val songs = storage.getData()?.toMutableList() ?: mutableListOf()
        songs.removeIf { it.trackId == track.trackId }
        songs.add(0, track)
        val limitedSongs = ArrayList(songs.take(10))

        storage.storeData(limitedSongs)
    }

    override fun getHistory(): Resource<List<Track>> {
        val songs = storage.getData() ?: listOf()
        return Resource.Success(songs)
    }

    override fun clearHistory() {
        storage.clearData()
    }
}