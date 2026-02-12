package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.SongInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SongsState

class SongsViewModel(
    private val songInteractor: SongInteractor,
    private val historyInteractor: HistoryInteractor
): ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
    private var latestSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SongsState>()
    fun observeState(): LiveData<SongsState> = stateLiveData

    fun saveToHistory(track: Track) {
        historyInteractor.saveToHistory(track)
    }

    fun getHistory() {
        historyInteractor.getHistory(object: HistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>?) {
                renderState(
                    SongsState.Content(searchHistory ?: emptyList(), true)
                )
            }
        })
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        renderState(
            SongsState.Content(emptyList())
        )
    }

    fun searchNow(query: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        latestSearchText = query
        searchRequest(query)
    }

    fun searchDebounce(changedText: String) {
        if (changedText == latestSearchText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun normalizeString(query: String?): String {
        if (query == null) return ""

        return query
            .replace(Regex("[^a-zA-Zа-яА-Я0-9\\s'\".,&-]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun searchRequest(newSearchText: String) {
        val query = normalizeString(newSearchText)

        if (query.isEmpty()) return

        renderState(
            SongsState.Loading
        )

        songInteractor.findSongs(query, object : SongInteractor.SongsConsumer {
            override fun consume(foundSongs: List<Track>?, errorMessage: String?) {
                handler.post {
                    val songs = mutableListOf<Track>()
                    if (foundSongs != null) {
                        songs.addAll(foundSongs)
                    }

                    when {
                        errorMessage != null -> {
                            renderState(
                                SongsState.Error(errorMessage)
                            )
                        }

                        songs.isEmpty() -> {
                            renderState(
                                SongsState.Empty("not_found")
                            )
                        }

                        else -> {
                            renderState(
                                SongsState.Content(
                                    songs
                                )
                            )
                        }
                    }
                }
            }
        })
    }

    private fun renderState(state: SongsState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
}