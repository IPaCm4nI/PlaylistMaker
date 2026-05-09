package com.example.playlistmaker.playlist.data.converters

import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.models.Playlist

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            namePlaylist = playlist.namePlaylist,
            descriptionPlaylist = playlist.descriptionPlaylist,
            pathToImage = playlist.pathToImage,
            tracksInPlaylist = playlist.tracksInPlaylist,
            countTracks = playlist.countTracks
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            namePlaylist = playlist.namePlaylist,
            descriptionPlaylist = playlist.descriptionPlaylist,
            pathToImage = playlist.pathToImage,
            tracksInPlaylist = playlist.tracksInPlaylist,
            countTracks = playlist.countTracks
        )
    }
}