package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface SongInteractor {
    fun findSongs(query: String): Flow<Pair<List<Track>?, String?>>
}