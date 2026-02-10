package com.example.playlistmaker.settings.data

interface StorageClient {
    fun storeData(isTheme: Boolean)
    fun getData(): Boolean
}