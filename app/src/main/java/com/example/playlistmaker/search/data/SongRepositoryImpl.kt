package com.example.playlistmaker.search.data

import com.example.playlistmaker.creator.Resource
import com.example.playlistmaker.search.domain.api.SongRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.data.dto.SongRequest
import com.example.playlistmaker.search.data.dto.SongResponse
import java.text.SimpleDateFormat
import java.util.Locale

class SongRepositoryImpl(private val networkClient: NetworkClient) : SongRepository {
    override fun findSongs(query: String): Resource<List<Track>> {
        val response = networkClient.doRequest(SongRequest(query))
        // TODO Проверить без интернета, отдельная ошибка
        return when (response.resultCode) {
            200 -> {
                Resource.Success((response as SongResponse).results.map {
                    Track(
                        trackId = it.trackId, trackName = it.trackName, artistName = it.artistName,
                        trackTimeMillis = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(it.trackTimeMillis),
                        artworkUrl100 = it.artworkUrl100, collectionName = it.collectionName,
                        releaseDate = it.releaseDate, primaryGenreName = it.primaryGenreName,
                        country = it.country, previewUrl = it.previewUrl
                    )
                    }
                )
            }
            else -> {
                Resource.Error("not_connection")
            }
        }

//        if (response.resultCode == 200) {
//            return (response as SongResponse).results.map {
//                Track(trackId = it.trackId, trackName = it.trackName, artistName = it.artistName,
//                    trackTimeMillis = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
//                    artworkUrl100 = it.artworkUrl100, collectionName = it.collectionName,
//                    releaseDate = it.releaseDate, primaryGenreName = it.primaryGenreName,
//                    country = it.country, previewUrl = it.previewUrl)
//            }
//        } else {
//            return emptyList()
//        }
    }
}