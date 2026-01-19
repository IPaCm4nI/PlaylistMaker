package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.HistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class HistoryRepositoryImpl(private val sharedPrefs: SharedPreferences): HistoryRepository {
    companion object {
        private const val KEY_HISTORY_TRACK = "key_history_track"
    }

    override fun saveTrack(track: Track) {
        val jsonFromShared = sharedPrefs.getString(KEY_HISTORY_TRACK, null) ?: emptyList<Track>().toString()
        var listTrackFromShared = Gson().fromJson(jsonFromShared, Array<Track>::class.java).toMutableList()

        listTrackFromShared.removeIf { it.trackId == track.trackId }
        listTrackFromShared.add(0, track)
        listTrackFromShared = listTrackFromShared.take(10).toMutableList()

        sharedPrefs
            .edit()
            .putString(KEY_HISTORY_TRACK, Gson().toJson(listTrackFromShared))
            .apply()
    }

    override fun getHistory(): MutableList<Track> {
        val jsonFromShared = sharedPrefs.getString(KEY_HISTORY_TRACK, null) ?: emptyList<Track>().toString()
        return Gson().fromJson(jsonFromShared, Array<Track>::class.java).toMutableList()
    }

    override fun clearHistory() {
        sharedPrefs
            .edit()
            .remove(KEY_HISTORY_TRACK)
            .apply()
    }
}