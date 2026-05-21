package com.example.playlistmaker.utils

fun Int.toTracksCountString(): String {
    val lastTwo = this % 100
    val last = this % 10
    return "$this " + when {
        lastTwo in 11..14 -> "треков"
        last == 1 -> "трек"
        last in 2..4 -> "трека"
        else -> "треков"
    }
}
