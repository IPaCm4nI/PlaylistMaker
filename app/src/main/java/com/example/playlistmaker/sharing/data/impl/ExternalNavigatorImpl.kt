package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.core.net.toUri
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(
    private val context: Context
): ExternalNavigator {
    override fun openLink(uri: String) {
        val intentSupport = Intent(Intent.ACTION_VIEW, uri.toUri())
        intentSupport.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intentSupport)
    }

    override fun openEmail(
        emailData: EmailData
    ) {
        val intentSupport = Intent(Intent.ACTION_SENDTO)
        intentSupport.data = Uri.parse("mailto:")
        intentSupport.putExtra(Intent.EXTRA_EMAIL, emailData.email)
        intentSupport.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        intentSupport.putExtra(Intent.EXTRA_TEXT, emailData.text)
        intentSupport.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intentSupport)
    }

    override fun shareLink(title: String, share: String) {
        val intentShare = Intent(Intent.ACTION_SEND)
        intentShare.type = "text/plain"
        intentShare.putExtra(Intent.EXTRA_TEXT,share)
        intentShare.addFlags(FLAG_ACTIVITY_NEW_TASK)
        val chooser = Intent.createChooser(intentShare, title)
        chooser.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}