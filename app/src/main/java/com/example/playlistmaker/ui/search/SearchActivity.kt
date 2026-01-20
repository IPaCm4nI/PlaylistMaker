package com.example.playlistmaker.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.data.dto.SongResponse
import com.example.playlistmaker.data.network.SongApi
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.SongInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.player.PlayerActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var searchText: String = SEARCH_DEF
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var placeholderId: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderLayout: LinearLayout
    private lateinit var toolbarSearchId: MaterialToolbar
    private lateinit var recyclerViewSongs: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var editTextId: EditText
    private lateinit var clearButtonId: ImageView
    private lateinit var updateButton: Button
    private lateinit var clearHistory: Button
    private lateinit var layoutHistory: LinearLayout
    private lateinit var progressBar: ProgressBar
    companion object {
        const val KEY_SEARCH = "SEARCH"
        const val SEARCH_DEF = ""

        const val KEY_TRACK = "TRACK"

        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        val FILE_HISTORY_TRACK = "file_history_track"
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }
    private lateinit var songsInteractor: SongInteractor
    private lateinit var historyInteractor: HistoryInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        songsInteractor = Creator.provideSongsInteractor()
        historyInteractor = Creator.provideHistoryInteractor()

        searchAdapter = TrackAdapter {
            historyInteractor.saveTrack(it)

            moveToPlayer(it)
        }

        historyAdapter = TrackAdapter {
            historyInteractor.saveTrack(it)

            moveToPlayer(it)
        }

        recyclerViewSongs = findViewById(R.id.listSongs)
        recyclerViewSongs.adapter = searchAdapter

        recyclerViewHistory = findViewById(R.id.listHistory)
        recyclerViewHistory.adapter = historyAdapter

        editTextId = findViewById(R.id.search)
        clearButtonId = findViewById(R.id.clearIcon)
        placeholderId = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderLayout = findViewById(R.id.placeholderLayout)
        toolbarSearchId = findViewById(R.id.toolbar_search)
        clearHistory = findViewById(R.id.bClearHistory)
        updateButton = findViewById(R.id.bUpdate)
        layoutHistory = findViewById(R.id.llHistory)
        progressBar = findViewById(R.id.progressBar)

        toolbarSearchId.setNavigationOnClickListener {
            hideKeyboard()
            finish()
        }

        clearButtonId.setOnClickListener {
            editTextId.setText("")
            editTextId.requestFocus()

            progressBar.isVisible = false

            showHistory()
            searchAdapter.updateItem(mutableListOf())

            hideKeyboard()
        }

        editTextId.doOnTextChanged { text, _, _, _ ->
            clearButtonId.isVisible = !text.isNullOrEmpty()

            if (text.isNullOrEmpty() && editTextId.hasFocus()) {
                showHistory()
            } else {
                searchDebounce()
                showResults()
            }
        }

        editTextId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editTextId.text.isNullOrEmpty()) {
                showHistory()
            } else {
                showResults()
            }
        }

        updateButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                searchRequest()
            }
        }

        clearHistory.setOnClickListener {
            historyInteractor.clearHistory()
            historyAdapter.updateItem(mutableListOf())
            layoutHistory.isVisible = false
        }
    }

    private fun normalizeString(query: String?): String {
        if (query == null) return ""

        return query
            .replace(Regex("[^a-zA-Zа-яА-Я0-9\\s'\".,&-]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(KEY_SEARCH, SEARCH_DEF)

        editTextId = findViewById(R.id.search)
        editTextId.setText(searchText)

        if(searchText.isNotEmpty()) {
            editTextId.setSelection(searchText.length)
            searchRequest()
        }
    }

    private fun showPage(text: String) {
        if (text.isNotEmpty()) {
            when(text) {
                "not_found" -> {
                    placeholderImage.setImageResource(R.drawable.not_found)
                    placeholderId.text = getString(R.string.nothing_search)
                }
                "not_connection" -> {
                    placeholderImage.setImageResource(R.drawable.not_connection)
                    placeholderId.text = getString(R.string.trouble_network)
                    updateButton.isVisible = true
                }
            }

            placeholderLayout.isVisible = true
            searchAdapter.updateItem(mutableListOf())
        } else {
            showResults()
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showHistory() {
        val historyMovies = historyInteractor.getHistory()
        historyAdapter.updateItem(historyMovies)
        layoutHistory.isVisible = historyMovies.isNotEmpty()
        recyclerViewSongs.isVisible = false
        placeholderLayout.isVisible = false
        updateButton.isVisible = false
    }

    private fun showResults() {
        recyclerViewSongs.isVisible = true
        layoutHistory.isVisible = false
        placeholderLayout.isVisible = false
        updateButton.isVisible = false
    }

    private fun moveToPlayer(track: Track) {
        if (clickDebounce()) {
            val intentPlayer = Intent(this, PlayerActivity::class.java)
            intentPlayer.putExtra(KEY_TRACK, Gson().toJson(track))
            startActivity(intentPlayer)
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchRequest() {
        val query = normalizeString(editTextId.text.toString())
        searchText = query

        if (query.isEmpty()) return

        progressBar.isVisible = true
        placeholderLayout.isVisible = false

        songsInteractor.findSongs(query, object : SongInteractor.SongsConsumer {
            override fun consume(foundSongs: List<Track>) {
                runOnUiThread {
                    progressBar.isVisible = false

                    if (foundSongs.isNotEmpty()) {
                        searchAdapter.updateItem(foundSongs.toMutableList())
                        showResults()
                    } else {
                        showPage("not_found")
                    }
                }
            }
        })
    }
}