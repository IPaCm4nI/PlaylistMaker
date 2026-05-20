package com.example.playlistmaker.playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val playlistId: Int
) : ViewModel() {

    private val mutablePlaylistLiveData = MutableLiveData<Playlist>()
    val observerPlaylistLiveData: LiveData<Playlist> = mutablePlaylistLiveData

    private val mutableCountMinuteTracksLiveData = MutableLiveData<Int>()
    val observerCountMinuteTracksLiveData: LiveData<Int> = mutableCountMinuteTracksLiveData

    private val mutableTracksLiveData = MutableLiveData<List<Track>>()
    val observerTracksLiveData: LiveData<List<Track>> = mutableTracksLiveData

    init {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            mutablePlaylistLiveData.postValue(playlist)

            interactor.getAddedTracks(playlist.trackIds).collect { tracks ->
                val durationSum = tracks.sumOf { track ->
                    getTrackDurationMillis(track.trackTimeMillis)
                }

                val durationMinutes = durationSum / 60000

                mutableCountMinuteTracksLiveData.postValue(durationMinutes.toInt())
                mutableTracksLiveData.postValue(tracks)

            }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            interactor.deleteTrack(track.trackId, playlistId)

            val currentTracks = mutableTracksLiveData.value
                ?.filter { it.trackId != track.trackId }
                ?: emptyList()
            mutableTracksLiveData.postValue(currentTracks)

            val durationSum = currentTracks.sumOf { getTrackDurationMillis(it.trackTimeMillis) }
            mutableCountMinuteTracksLiveData.postValue((durationSum / 60000).toInt())

            val updatedPlaylist = interactor.getPlaylistById(playlistId)
            mutablePlaylistLiveData.postValue(updatedPlaylist)
        }
    }

    private fun getTrackDurationMillis(trackTime: String): Long {
        val parts = trackTime.split(":")
        if (parts.size != 2) return trackTime.toLongOrNull() ?: 0L

        val minutes = parts[0].toLongOrNull() ?: 0L
        val seconds = parts[1].toLongOrNull() ?: 0L

        return (minutes * 60 + seconds) * 1000
    }
}
