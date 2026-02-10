package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.ui.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val url: String): ViewModel() {
    private val mediaPlayer = MediaPlayer()

    private val handler = Handler(Looper.getMainLooper())

    private val dateFormatStart = SimpleDateFormat("mm:ss", Locale.getDefault()).format(0).trim()

    private val currentTimeRunnable = object: Runnable {
        override fun run() {
            if (playerState.value?.state != STATE_PLAYING) return

            playerState.postValue(
                PlayerState(
                    STATE_PLAYING,
                    false,
                    getProgress()
                )
            )

            handler.postDelayed(this, DELAY_UPDATE_TIMER)
        }
    }

    init {
        preparePlayer()
    }

    private val playerState = MutableLiveData(
        PlayerState(
            STATE_DEFAULT,
            true,
            dateFormatStart
        )
    )
    fun observerPlayerState(): LiveData<PlayerState> = playerState

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState.postValue(
                PlayerState(
                    STATE_PREPARED,
                    true,
                    dateFormatStart
                )
            )
        }
        mediaPlayer.setOnCompletionListener {
            resetTimer()
        }
    }

    private fun startPlayer() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }

        playerState.postValue(
            PlayerState(
                STATE_PLAYING,
                false,
                getProgress()
            )
        )

        handler.removeCallbacks(currentTimeRunnable)
        handler.postDelayed(currentTimeRunnable, DELAY_UPDATE_TIMER)
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()

        playerState.postValue(
            PlayerState(
                STATE_PAUSED,
                true,
                getProgress()
            )
        )

        handler.removeCallbacks(currentTimeRunnable)
    }

    fun onPlayButtonClicked() {
        when(playerState.value?.state) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun resetTimer() {
        handler.removeCallbacks(currentTimeRunnable)
        playerState.postValue(
            PlayerState(
                STATE_PREPARED,
                true,
                dateFormatStart
            )
        )
    }

    private fun getProgress(): String {
        return try {
            SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(mediaPlayer.currentPosition)
        } catch (e: Exception) {
            dateFormatStart
        }
    }

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_UPDATE_TIMER = 500L

        fun getFactory(trackUrl: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(trackUrl)
            }
        }
    }
}