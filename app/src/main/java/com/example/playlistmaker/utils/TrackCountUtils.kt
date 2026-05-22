package com.example.playlistmaker.utils

import android.content.res.Resources
import com.example.playlistmaker.R

fun Int.toTracksCountString(resources: Resources): String =
    resources.getQuantityString(R.plurals.tracks_count, this, this)
