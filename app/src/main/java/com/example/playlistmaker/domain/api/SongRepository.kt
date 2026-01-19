package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SongRepository {
    fun findSongs(query: String): List<Track>
}