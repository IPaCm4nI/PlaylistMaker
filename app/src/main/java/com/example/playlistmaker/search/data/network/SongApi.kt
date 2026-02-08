package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.SongResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApi {

    @GET("/search?entity=song")
    fun getSongs(@Query("term") text: String) : Call<SongResponse>
}