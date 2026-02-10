package com.example.playlistmaker.sharing.domain.api

interface ResourceProvider {
    fun getString(id: Int): String
}