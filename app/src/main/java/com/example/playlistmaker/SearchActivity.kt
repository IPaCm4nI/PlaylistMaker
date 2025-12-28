package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import okhttp3.internal.http2.Http2Reader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var searchText: String = SEARCH_DEF
    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val songService = retrofit.create(SongApi::class.java)

    private val FILE_HISTORY_TRACK = "file_history_track"

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
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPrefs = getSharedPreferences(FILE_HISTORY_TRACK, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefs)

        searchAdapter = TrackAdapter {
            searchHistory.saveTrack(it)

            moveToPlayer(it)
        }

        historyAdapter = TrackAdapter {
            searchHistory.saveTrack(it)

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

            showHistory(searchHistory)
            searchAdapter.updateItem(mutableListOf())

            hideKeyboard()
        }

        editTextId.doOnTextChanged { text, _, _, _ ->
            clearButtonId.isVisible = !text.isNullOrEmpty()

            if (text.isNullOrEmpty() && editTextId.hasFocus()) {
                showHistory(searchHistory)
            } else {
                searchDebounce()
                showResults()
            }
        }

        editTextId.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editTextId.text.isNullOrEmpty()) {
                showHistory(searchHistory)
            } else {
                showResults()
            }
        }

//        editTextId.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                val query = normalizeString(editTextId.text.toString())
//                searchText = query
//
//                if (query.isNotEmpty()) {
//                    findSong(query)
//                }
//                true
//            } else {
//                false
//            }
//        }

        updateButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                findSong(searchText)
            }
        }

        clearHistory.setOnClickListener {
            searchHistory.clearHistory()
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

    private fun findSong(query: String) {
        progressBar.isVisible = true
        placeholderLayout.isVisible = false

        songService.getSongs(query)
            .enqueue(object: Callback<SongResponse> {
                override fun onResponse(
                    call: Call<SongResponse?>,
                    response: Response<SongResponse?>
                ) {
                    progressBar.isVisible = false

                    when(response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                searchAdapter.updateItem(response.body()?.results!!.toMutableList())
                                recyclerViewSongs.isVisible = true
                                showPage("")
                            } else {
                                showPage("not_found")
                            }
                        }
                        else -> {
                            showPage("not_connection")
                        }
                    }
                }

                override fun onFailure(
                    call: Call<SongResponse?>,
                    t: Throwable
                ) {
                    progressBar.isVisible = false

                    showPage("not_connection")
                }
            })
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
            findSong(searchText)
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
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showHistory(searchHistory: SearchHistory) {
        val historyMovies = searchHistory.getHistory()
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
        if (query.isNotEmpty()) {
            findSong(query)
        }
    }
}