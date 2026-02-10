package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class HistoryInteractorImpl(private val repository: HistoryRepository): HistoryInteractor {
    override fun saveToHistory(track: Track) {
        repository.saveToHistory(track)
    }

    override fun getHistory(consumer: HistoryInteractor.HistoryConsumer) {
        consumer.consume(repository.getHistory().data)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}