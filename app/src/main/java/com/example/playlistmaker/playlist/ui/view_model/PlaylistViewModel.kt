package com.example.playlistmaker.playlist.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor
): ViewModel() {
    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            interactor.insertPlaylist(playlist)
        }
    }
}