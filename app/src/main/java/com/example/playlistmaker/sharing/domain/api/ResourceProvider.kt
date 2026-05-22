package com.example.playlistmaker.sharing.domain.api

interface ResourceProvider {
    fun getString(id: Int): String
    fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String
}
