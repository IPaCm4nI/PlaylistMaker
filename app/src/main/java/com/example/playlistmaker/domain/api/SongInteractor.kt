package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SongInteractor {
    fun findSongs(query: String, consumer: SongsConsumer)

    interface SongsConsumer {
        fun consume(foundSongs: List<Track>)
    }
}