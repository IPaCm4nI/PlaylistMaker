package com.example.playlistmaker.search.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextWatcher
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
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SongsState
import com.example.playlistmaker.search.ui.view_model.SongsViewModel
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private var searchText: String = SEARCH_DEF
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
    }

    private val handler = Handler(Looper.getMainLooper())
    private var textWatcher: TextWatcher? = null
    private var isClickAllowed = true
    private val viewModel by viewModel<SongsViewModel>()
    private var searchAdapter = TrackAdapter {
        viewModel.saveToHistory(it)

        moveToPlayer(it)
    }
    private var historyAdapter = TrackAdapter {
        viewModel.saveToHistory(it)

        moveToPlayer(it)
    }

    private enum class Error {
        NOT_FOUND,
        NOT_CONNECTION
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerViewSongs = findViewById(R.id.listSongs)
        recyclerViewSongs.adapter = searchAdapter

        recyclerViewHistory = findViewById(R.id.listHistory)
        recyclerViewHistory.adapter = historyAdapter

        viewModel.observeState().observe(this) {
            render(it)
        }

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

        textWatcher = editTextId.doOnTextChanged { text, _, _, _ ->
            searchText = text?.toString() ?: ""

            clearButtonId.isVisible = !text.isNullOrEmpty()

            if (text.isNullOrEmpty() && editTextId.hasFocus()) {
                showHistory()
            } else {
                viewModel.searchDebounce(text?.toString() ?: "")
                showRecyclerSongs()
            }
        }

        editTextId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editTextId.text.isNullOrEmpty()) {
                showHistory()
            }
            else {
                showRecyclerSongs()
            }
        }

        clearButtonId.setOnClickListener {
            editTextId.setText("")
            editTextId.requestFocus()

            showHistory()

            hideKeyboard()
        }

        updateButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                viewModel.searchNow(searchText)
            }
        }

        clearHistory.setOnClickListener {
            viewModel.clearHistory()
            layoutHistory.isVisible = false
        }
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
            viewModel.searchDebounce(searchText)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { editTextId.removeTextChangedListener(it) }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showPage(text: String) {
        when(text) {
           Error.NOT_FOUND.name.lowercase() -> {
                placeholderImage.setImageResource(R.drawable.not_found)
                placeholderId.text = getString(R.string.nothing_search)
            }
            Error.NOT_CONNECTION.name.lowercase() -> {
                placeholderImage.setImageResource(R.drawable.not_connection)
                placeholderId.text = getString(R.string.trouble_network)
                updateButton.isVisible = true
            }
        }

        placeholderLayout.isVisible = true
        recyclerViewSongs.isVisible = false
        progressBar.isVisible = false
        layoutHistory.isVisible = false
    }

    private fun showHistory() {
        viewModel.getHistory()
    }

    private fun showResults(songs: List<Track>) {
        searchAdapter.updateItem(songs.toMutableList())

        progressBar.isVisible = false
        recyclerViewSongs.isVisible = true
        layoutHistory.isVisible = false
        placeholderLayout.isVisible = false
        updateButton.isVisible = false
    }

    private fun showRecyclerSongs() {
        recyclerViewSongs.isVisible = true
        layoutHistory.isVisible = false
        placeholderLayout.isVisible = false
        updateButton.isVisible = false
    }

    private fun showLoading() {
        recyclerViewSongs.isVisible = false
        layoutHistory.isVisible = false
        placeholderLayout.isVisible = false
        progressBar.isVisible = true
    }

    private fun showHistory(tracks: List<Track>) {
        historyAdapter.updateItem(tracks.toMutableList())
        layoutHistory.isVisible = tracks.isNotEmpty()

        recyclerViewSongs.isVisible = false
        placeholderLayout.isVisible = false
        progressBar.isVisible = false
        updateButton.isVisible = false
    }

    private fun moveToPlayer(track: Track) {
        if (clickDebounce()) {
            val intentPlayer = Intent(this, PlayerActivity::class.java)
            intentPlayer.putExtra(KEY_TRACK, Gson().toJson(track))
            startActivity(intentPlayer)
        }
    }

    private fun render(state: SongsState) {
        when(state) {
            is SongsState.Loading -> showLoading()
            is SongsState.Error -> showPage(state.errorMessage)
            is SongsState.Empty -> showPage(state.message)
            is SongsState.Content -> {
                if (state.isHistory) {
                    showHistory(state.songs)
                } else {
                    showResults(state.songs)
                }
            }
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
}