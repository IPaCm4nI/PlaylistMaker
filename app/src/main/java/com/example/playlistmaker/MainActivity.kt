package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search)
        val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        buttonSearch.setOnClickListener(buttonSearchClickListener)

        val buttonMediateka = findViewById<Button>(R.id.mediateka)
        buttonMediateka.setOnClickListener {
            val mediaIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(mediaIntent)
        }

        val buttonSettings = findViewById<Button>(R.id.settings)
        buttonSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}