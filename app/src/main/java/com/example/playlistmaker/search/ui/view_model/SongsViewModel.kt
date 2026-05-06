package com.example.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.SongInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SongsState
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SongsViewModel(
    private val songInteractor: SongInteractor,
    private val historyInteractor: HistoryInteractor
): ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
    private var latestSearchText: String? = null
    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true) { changedText ->
        searchRequest(changedText)
    }
    private val stateLiveData = MutableLiveData<SongsState>()
    fun observeState(): LiveData<SongsState> = stateLiveData

    fun saveToHistory(track: Track) {
        historyInteractor.saveToHistory(track)
    }

    fun getHistory() {
        viewModelScope.launch {
            historyInteractor.getHistory(object : HistoryInteractor.HistoryConsumer {
                override fun consume(searchHistory: List<Track>?) {
                    renderState(
                        SongsState.Content(searchHistory ?: emptyList(), true)
                    )
                }
            })
        }
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        renderState(
            SongsState.Content(emptyList())
        )
    }

    fun searchNow(query: String) {
        latestSearchText = query
        trackSearchDebounce(query)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            trackSearchDebounce(changedText)
        }
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

        viewModelScope.launch {
            songInteractor
                .findSongs(query)
                .collect { pair ->
                    processResult(pair.first, pair.second)
                }
        }
    }

    private fun processResult(foundSongs: List<Track>?, errorMessage: String?) {
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

    private fun renderState(state: SongsState) {
        stateLiveData.postValue(state)
    }
}