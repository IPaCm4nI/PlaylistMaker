package com.example.playlistmaker.playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val interactor: PlaylistInteractor
): CreatePlaylistViewModel(interactor) {

    private val mutablePlaylistLiveData = MutableLiveData<Playlist>()
    val playlistLiveData: LiveData<Playlist> = mutablePlaylistLiveData

    fun initPlaylist(playlist: Playlist) {
        mutablePlaylistLiveData.value = playlist
    }

    fun updatePlaylist(playlist: Playlist, onComplete: () -> Unit) {
        viewModelScope.launch {
            interactor.updatePlaylist(playlist)
            onComplete()
        }
    }
}