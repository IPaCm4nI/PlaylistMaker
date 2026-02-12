package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.api.SongInteractor
import com.example.playlistmaker.search.domain.api.SongRepository
import java.util.concurrent.Executor

class SongInteractorImpl(
    private val repository: SongRepository,
    private val executor: Executor
): SongInteractor {

    override fun findSongs(
        query: String,
        consumer: SongInteractor.SongsConsumer
    ) {
        executor.execute {
            when(val resource = repository.findSongs(query)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
            }
        }
    }

}