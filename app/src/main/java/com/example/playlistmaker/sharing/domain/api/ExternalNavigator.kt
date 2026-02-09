package com.example.playlistmaker.sharing.domain.api

import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun openLink(uri: String)
    fun openEmail(emailData: EmailData)
    fun shareLink(title: String, share: String)
}