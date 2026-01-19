package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface HistoryRepository {
    fun saveTrack(track: Track)
    fun getHistory(): MutableList<Track>
    fun clearHistory()
}