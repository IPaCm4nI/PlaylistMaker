package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbarSettingsId = findViewById<MaterialToolbar>(R.id.toolbar_settings)
        toolbarSettingsId.setNavigationOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        val shareButton = findViewById<TextView>(R.id.share)
        shareButton.setOnClickListener {
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.type = "text/plain"
            intentShare.putExtra(Intent.EXTRA_TEXT,getString(R.string.link_to_praktikum))
            startActivity(Intent.createChooser(intentShare, getString(R.string.title_share)))
        }

        val supportButton = findViewById<TextView>(R.id.support)
        supportButton.setOnClickListener {
            val intentSupport = Intent(Intent.ACTION_SENDTO)
            intentSupport.data = Uri.parse("mailto:")
            intentSupport.putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.support_mail))
            intentSupport.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_mail))
            intentSupport.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_mail))
            startActivity(intentSupport)
        }

        val contractButton = findViewById<TextView>(R.id.contract)
        contractButton.setOnClickListener {
            val intentSupport = Intent(Intent.ACTION_VIEW, getString(R.string.link_offer).toUri())
            startActivity(intentSupport)
        }

    }
}