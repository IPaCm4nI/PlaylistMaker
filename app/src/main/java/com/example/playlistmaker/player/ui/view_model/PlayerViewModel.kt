package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.api.FavouriteTrackInteractor
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.TrackInPlaylistState
import com.example.playlistmaker.playlist.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val url: String,
    private val mediaPlayer: MediaPlayer,
    private val gson: Gson,
    private val favouriteTrackInteractor: FavouriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {
    private var timerJob: Job? = null
    private var playlistsJob: Job? = null
    private val playerState = MutableLiveData<PlayerState>(
        PlayerState.Default()
    )
    fun observerPlayerState(): LiveData<PlayerState> = playerState

    private val isFavourite = MutableLiveData<Boolean>()
    fun observerIsFavourite(): LiveData<Boolean> = isFavourite

    private val mutablePlaylistsState = MutableLiveData<PlaylistsState>()
    fun observerPlaylistsState(): LiveData<PlaylistsState> = mutablePlaylistsState

    private val mutableExistsTrack = MutableLiveData<TrackInPlaylistState>()
    fun observerExistsTrack(): LiveData<TrackInPlaylistState> = mutableExistsTrack

    private var isPlayerInitialized = false
    fun preparePlayer() {
        if (isPlayerInitialized) return
        isPlayerInitialized = true

        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState.postValue(
                PlayerState.Prepared()
            )
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            mediaPlayer.seekTo(0)
            playerState.postValue(
                PlayerState.Prepared()
            )
        }
    }

    private fun startPlayer() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        playerState.postValue(
            PlayerState.Playing(getProgress())
        )

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                playerState.postValue(
                    PlayerState.Playing(getProgress())
                )
                delay(DELAY_UPDATE_TIMER)
            }
        }
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()

        timerJob?.cancel()

        playerState.postValue(
            PlayerState.Paused(getProgress())
        )
    }

    fun onPlayButtonClicked() {
        when(playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
    }

    private fun getProgress(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playerState.value = PlayerState.Default()
    }

    fun checkFavourite(trackId: Int) {
        viewModelScope.launch {
            favouriteTrackInteractor.getTracks().collect { tracks ->
                isFavourite.postValue(tracks.any { it.trackId == trackId })
            }
        }
    }

    fun onFavouriteClicked(track: Track) {
        viewModelScope.launch {
            val currentFavourite = isFavourite.value ?: false

            if (currentFavourite) {
                favouriteTrackInteractor.deleteTrack(track.trackId)
            } else {
                favouriteTrackInteractor.insertTrack(track)
            }

            isFavourite.postValue(!currentFavourite)
        }
    }

    fun getPlaylists() {
        playlistsJob?.cancel()
        playlistsJob = viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                renderState(playlists)
            }
        }
    }

    private fun renderState(playlists: List<Playlist>) {
        mutablePlaylistsState.postValue(
            PlaylistsState.Content(playlists)
        )
    }

    fun checkExistsTrack(playlist: Playlist, track: Track) {
        val trackIds = getTrackIdsFromPlaylist(playlist)

        val exists = trackIds.contains(track.trackId)

        if (exists) {
            mutableExistsTrack.postValue(
                TrackInPlaylistState.Exists(playlist.namePlaylist)
            )
        } else {
            viewModelScope.launch {
                playlistInteractor.insertNewTrackInPlaylist(playlist, track)
                mutableExistsTrack.postValue(
                    TrackInPlaylistState.NotExists(playlist.namePlaylist)
                )
            }
        }
    }

    private fun getTrackIdsFromPlaylist(playlist: Playlist): List<Int> {
        if (playlist.tracksInPlaylist.isBlank()) {
            return emptyList()
        }

        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(playlist.tracksInPlaylist, type) ?: emptyList()
    }

    companion object {
        private const val DELAY_UPDATE_TIMER = 300L
    }
}