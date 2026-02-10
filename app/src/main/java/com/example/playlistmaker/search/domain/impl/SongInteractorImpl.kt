package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.api.SongInteractor
import com.example.playlistmaker.search.domain.api.SongRepository
import java.util.concurrent.Executors

class SongInteractorImpl(private val repository: SongRepository): SongInteractor {
    val executor = Executors.newCachedThreadPool()

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