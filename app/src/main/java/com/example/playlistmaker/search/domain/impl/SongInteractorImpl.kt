package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.search.domain.api.SongInteractor
import com.example.playlistmaker.search.domain.api.SongRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongInteractorImpl(
    private val repository: SongRepository
): SongInteractor {

    override fun findSongs(
        query: String
    ) : Flow<Pair<List<Track>?, String?>> {
        return repository.findSongs(query).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}