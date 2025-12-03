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
import com.example.playlistmaker.App.Companion.PREFERENCES_FILE
import com.example.playlistmaker.App.Companion.THEME_KEY
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var toolbarSettingsId: MaterialToolbar
    private lateinit var shareButton: TextView
    private lateinit var supportButton: TextView
    private lateinit var contractButton: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        themeSwitcher = findViewById(R.id.themeSwitcher)
        toolbarSettingsId = findViewById(R.id.toolbar_settings)
        shareButton = findViewById(R.id.share)
        supportButton = findViewById(R.id.support)
        contractButton = findViewById(R.id.contract)

        val sharedRefs = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)

        themeSwitcher.isChecked = sharedRefs.getBoolean(THEME_KEY, false)
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }

        toolbarSettingsId.setNavigationOnClickListener {
            finish()
        }

        shareButton.setOnClickListener {
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.type = "text/plain"
            intentShare.putExtra(Intent.EXTRA_TEXT,getString(R.string.link_to_praktikum))
            startActivity(Intent.createChooser(intentShare, getString(R.string.title_share)))
        }

        supportButton.setOnClickListener {
            val intentSupport = Intent(Intent.ACTION_SENDTO)
            intentSupport.data = Uri.parse("mailto:")
            intentSupport.putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.support_mail))
            intentSupport.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_mail))
            intentSupport.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_mail))
            startActivity(intentSupport)
        }

        contractButton.setOnClickListener {
            val intentSupport = Intent(Intent.ACTION_VIEW, getString(R.string.link_offer).toUri())
            startActivity(intentSupport)
        }

    }
}