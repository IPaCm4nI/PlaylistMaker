package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var toolbarSettingsId: MaterialToolbar
    private lateinit var shareButton: TextView
    private lateinit var supportButton: TextView
    private lateinit var contractButton: TextView
    private var viewModel: SettingsViewModel? = null
    private var suppressThemeListener = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory())
            .get(SettingsViewModel::class.java)

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

        viewModel?.observerIsTheme()?.observe(this) {
            suppressThemeListener = true
            themeSwitcher.isChecked = it
            suppressThemeListener = false
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            if (suppressThemeListener) return@setOnCheckedChangeListener
            viewModel?.setTheme(checked)
        }

        shareButton.setOnClickListener {
            viewModel?.shareApp()
        }

        supportButton.setOnClickListener {
            viewModel?.openSupport()
        }

        contractButton.setOnClickListener {
            viewModel?.openTerms()
        }

        toolbarSettingsId.setNavigationOnClickListener {
            finish()
        }
    }
}