package com.example.playlistmaker.mediateka.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState

class PlaylistsViewModel: ViewModel() {
    private val mutablePlaylistsState = MutableLiveData<PlaylistsState>()
    fun observerPlaylistsState(): LiveData<PlaylistsState> = mutablePlaylistsState

    // TODO Временная заглушка
    init {
        mutablePlaylistsState.postValue(
            PlaylistsState.Empty
        )
    }
}