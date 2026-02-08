package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SongInteractor {
    fun findSongs(query: String, consumer: SongsConsumer)

    interface SongsConsumer {
        fun consume(foundSongs: List<Track>?, errorMessage: String?)
    }
}