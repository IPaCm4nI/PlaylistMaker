package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPrefs: SharedPreferences) {
    private val KEY_HISTORY_TRACK = "key_history_track"

    fun saveTrack(track: Track) {
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

    fun getHistory(): MutableList<Track> {
        val jsonFromShared = sharedPrefs.getString(KEY_HISTORY_TRACK, null) ?: emptyList<Track>().toString()
        return Gson().fromJson(jsonFromShared, Array<Track>::class.java).toMutableList()
    }

    fun clearHistory() {
        sharedPrefs
            .edit()
            .remove(KEY_HISTORY_TRACK)
            .apply()

    }
}