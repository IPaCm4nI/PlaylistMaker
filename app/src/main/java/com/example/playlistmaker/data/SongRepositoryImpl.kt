package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.SongRequest
import com.example.playlistmaker.data.dto.SongResponse
import com.example.playlistmaker.domain.api.SongRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class SongRepositoryImpl(private val networkClient: NetworkClient) : SongRepository {
    override fun findSongs(query: String): List<Track> {
        val response = networkClient.doRequest(SongRequest(query))

        if (response.resultCode == 200) {
            return (response as SongResponse).results.map {
                Track(trackId = it.trackId, trackName = it.trackName, artistName = it.artistName,
                    trackTimeMillis = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                    artworkUrl100 = it.artworkUrl100, collectionName = it.collectionName,
                    releaseDate = it.releaseDate, primaryGenreName = it.primaryGenreName,
                    country = it.country, previewUrl = it.previewUrl)
            }
        } else {
            return emptyList()
        }
    }
}