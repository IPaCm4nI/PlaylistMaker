package com.example.playlistmaker.search.data

import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.search.domain.api.SongRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.data.dto.SongRequest
import com.example.playlistmaker.search.data.dto.SongResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class SongRepositoryImpl(private val networkClient: NetworkClient) : SongRepository {
    override fun findSongs(query: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(SongRequest(query))
        when (response.resultCode) {
            200 -> {
                with(response as SongResponse) {
                    val data = response.results.map {
                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTimeMillis = SimpleDateFormat(
                                "mm:ss",
                                Locale.getDefault()
                            ).format(it.trackTimeMillis),
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            releaseDate = it.releaseDate,
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl
                        )
                    }

                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error("not_connection"))
            }
        }
    }
}