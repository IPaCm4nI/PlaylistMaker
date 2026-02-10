package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.models.Track

interface HistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): Resource<List<Track>>
    fun clearHistory()
}