package com.example.playlistmaker.playlist.data.converters

import com.example.playlistmaker.playlist.data.db.entity.AddedTracksEntity
import com.example.playlistmaker.search.domain.models.Track

class AddedTrackDbConvertor {
    fun map(track: Track): AddedTracksEntity {
        return AddedTracksEntity(
            track.trackId,
            track.artworkUrl100,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackTimeMillis,
            track.previewUrl,
            track.addedAt,
            track.isFavourite
        )
    }

    fun map(track: AddedTracksEntity): Track {
        return Track(
            trackId = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            addedAt = track.addedAt,
            isFavourite = track.isFavourite
        )
    }
}