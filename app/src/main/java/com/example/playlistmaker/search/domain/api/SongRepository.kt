package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface SongRepository {
    fun findSongs(query: String): Resource<List<Track>>
}