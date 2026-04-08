package com.example.playlistmaker.player.ui.models

sealed class PlayerState(val isPlayButton: Boolean, val progress: String) {

    class Default : PlayerState(true, "00:00")

    class Prepared : PlayerState(true, "00:00")

    class Playing(progress: String) : PlayerState(false, progress)

    class Paused(progress: String) : PlayerState(true, progress)
}
