package com.example.playlistmaker.mediateka.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState
import com.example.playlistmaker.playlist.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val interactor: PlaylistInteractor
): ViewModel() {
    private val mutablePlaylistsState = MutableLiveData<PlaylistsState>()
    fun observerPlaylistsState(): LiveData<PlaylistsState> = mutablePlaylistsState

    init {
        viewModelScope.launch {
            interactor.getPlaylists().collect { playlists ->
                renderState(playlists)
            }
        }
    }

    private fun renderState(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            mutablePlaylistsState.postValue(
                PlaylistsState.Empty
            )
        } else {
            mutablePlaylistsState.postValue(
                PlaylistsState.Content(playlists)
            )
        }
    }
}