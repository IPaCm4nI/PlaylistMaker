package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryRepository {
    fun saveTrack(track: Track)
    fun getHistory(): MutableList<Track>
    fun clearHistory()
}