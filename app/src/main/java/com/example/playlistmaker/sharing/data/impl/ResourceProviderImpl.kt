package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import com.example.playlistmaker.sharing.domain.api.ResourceProvider

class ResourceProviderImpl(
    private val context: Context
): ResourceProvider {
    override fun getString(id: Int): String {
        return context.getString(id)
    }
}