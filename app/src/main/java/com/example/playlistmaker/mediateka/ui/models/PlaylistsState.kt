package com.example.playlistmaker.mediateka.ui.models

import com.example.playlistmaker.playlist.domain.models.Playlist

sealed class PlaylistsState {
   object Empty: PlaylistsState()

   data class Content(
      val playlists: List<Playlist>
   ): PlaylistsState()
}