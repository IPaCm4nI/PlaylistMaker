package com.example.playlistmaker.playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val externalNavigator: ExternalNavigator,
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

    fun refreshPlaylist() {
        viewModelScope.launch {
            mutablePlaylistLiveData.postValue(interactor.getPlaylistById(playlistId))
        }
    }

    private val mutablePlaylistDeletedLiveData = MutableLiveData(false)
    val playlistDeletedLiveData: LiveData<Boolean> = mutablePlaylistDeletedLiveData

    fun sharePlaylist(chooserTitle: String) {
        val tracks = mutableTracksLiveData.value ?: return
        val playlist = mutablePlaylistLiveData.value ?: return
        val sb = StringBuilder()
        sb.appendLine(playlist.namePlaylist)
        if (!playlist.descriptionPlaylist.isNullOrEmpty()) sb.appendLine(playlist.descriptionPlaylist)
        sb.appendLine(getTracksCountText(tracks.size))
        tracks.forEachIndexed { i, track ->
            sb.appendLine("${i + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})")
        }
        externalNavigator.shareLink(chooserTitle, sb.toString().trimEnd())
    }

    private fun getTracksCountText(count: Int): String {
        val lastTwo = count % 100
        val last = count % 10
        return "$count " + when {
            lastTwo in 11..14 -> "треков"
            last == 1 -> "трек"
            last in 2..4 -> "трека"
            else -> "треков"
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            interactor.deletePlaylist(playlistId)
            mutablePlaylistDeletedLiveData.postValue(true)
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
