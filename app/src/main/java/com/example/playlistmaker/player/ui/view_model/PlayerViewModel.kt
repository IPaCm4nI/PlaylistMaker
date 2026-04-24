package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.api.FavouriteTrackInteractor
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val url: String,
    private val mediaPlayer: MediaPlayer,
    private val favouriteTrackInteractor: FavouriteTrackInteractor
): ViewModel() {
    private var timerJob: Job? = null
    private val playerState = MutableLiveData<PlayerState>(
        PlayerState.Default()
    )
    fun observerPlayerState(): LiveData<PlayerState> = playerState

    private val isFavourite = MutableLiveData<Boolean>()
    fun observerIsFavourite(): LiveData<Boolean> = isFavourite

    fun preparePlayer() {
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

    companion object {
        private const val DELAY_UPDATE_TIMER = 300L
    }
}