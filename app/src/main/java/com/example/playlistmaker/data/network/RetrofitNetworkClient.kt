package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.SongRequest

class RetrofitNetworkClient(private val songService: SongApi): NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is SongRequest) {
            val resp = songService.getSongs(dto.query).execute()
            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}