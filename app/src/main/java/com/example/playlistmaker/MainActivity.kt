package com.example.playlistmaker

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
                Toast.makeText(this@MainActivity, "Нажат поиск!", Toast.LENGTH_SHORT).show()
            }
        }
        buttonSearch.setOnClickListener(buttonSearchClickListener)

        val buttonMediateka = findViewById<Button>(R.id.mediateka)
        buttonMediateka.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажата медиатека!", Toast.LENGTH_SHORT).show()
        }

        buttonSearch.setOnClickListener(buttonSearchClickListener)
        val buttonSettings = findViewById<Button>(R.id.settings)
        buttonSettings.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажаты настройки!", Toast.LENGTH_SHORT).show()
        }
    }
}