package com.example.playlistmaker.playlist.data.converters

import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            namePlaylist = playlist.namePlaylist,
            descriptionPlaylist = playlist.descriptionPlaylist,
            pathToImage = playlist.pathToImage,
            tracksInPlaylist = gson.toJson(playlist.trackIds),
            countTracks = playlist.countTracks
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Int>>() {}.type
        val trackIds: List<Int> = if (playlist.tracksInPlaylist.isBlank()) {
            emptyList()
        } else {
            gson.fromJson(playlist.tracksInPlaylist, type) ?: emptyList()
        }
        return Playlist(
            id = playlist.id,
            namePlaylist = playlist.namePlaylist,
            descriptionPlaylist = playlist.descriptionPlaylist,
            pathToImage = playlist.pathToImage,
            trackIds = trackIds,
            countTracks = playlist.countTracks
        )
    }
}
