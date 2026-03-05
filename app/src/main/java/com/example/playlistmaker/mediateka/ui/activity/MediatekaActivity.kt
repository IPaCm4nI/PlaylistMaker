package com.example.playlistmaker.mediateka.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediatekaBinding
import com.example.playlistmaker.mediateka.ui.adapters.FragmentsAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MediatekaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediatekaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMediatekaBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonListeners()

        val adapter = FragmentsAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            when(pos) {
                0 -> tab.text = getString(R.string.favourite_track)
                1 -> tab.text = getString(R.string.playlists)
            }
        }.attach()
    }

    private fun buttonListeners() {
        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }
    }
}