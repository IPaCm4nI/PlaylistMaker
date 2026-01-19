package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SongInteractor
import com.example.playlistmaker.domain.api.SongRepository
import java.util.concurrent.Executors

class SongInteractorImpl(private val repository: SongRepository): SongInteractor {
    val executor = Executors.newCachedThreadPool()

    override fun findSongs(
        query: String,
        consumer: SongInteractor.SongsConsumer
    ) {
        executor.execute {
            consumer.consume(repository.findSongs(query))
        }
    }

}