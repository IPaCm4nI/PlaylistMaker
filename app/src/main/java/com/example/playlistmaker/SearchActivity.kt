package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
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

    private val trackAdapter = TrackAdapter()
    private lateinit var placeholderId: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var toolbarSearchId: MaterialToolbar
    private lateinit var recyclerViewSongs: RecyclerView
    private lateinit var editTextId: EditText
    private lateinit var clearButtonId: ImageView
    private lateinit var updateButton: Button


    companion object {
        const val KEY_SEARCH = "SEARCH"
        const val SEARCH_DEF = ""
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

        placeholderId = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        toolbarSearchId = findViewById(R.id.toolbar_search)
        toolbarSearchId.setNavigationOnClickListener {
            hideKeyboard()

            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        recyclerViewSongs = findViewById(R.id.listSongs)

        recyclerViewSongs.adapter = trackAdapter

        editTextId = findViewById(R.id.search)
        clearButtonId = findViewById(R.id.clearIcon)
        clearButtonId.setOnClickListener {
            editTextId.setText("")
            trackAdapter.updateItem(mutableListOf())

            hideKeyboard()
            placeholderId.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButtonId.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        editTextId.addTextChangedListener(searchTextWatcher)

        editTextId.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = normalizeString(editTextId.text.toString())
                searchText = query

                if (query.isNotEmpty()) {
                    findSong(query)
                }
                true
            } else {
                false
            }
        }

        updateButton = findViewById(R.id.bUpdate)
        updateButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                findSong(searchText)
            }
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
        songService.getSongs(query)
            .enqueue(object: Callback<SongResponse> {
                override fun onResponse(
                    call: Call<SongResponse?>,
                    response: Response<SongResponse?>
                ) {
                    when(response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackAdapter.updateItem(response.body()?.results!!.toMutableList())
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
                    updateButton.visibility = View.VISIBLE
                }
            }

            placeholderId.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE
            trackAdapter.updateItem(mutableListOf())
        } else {
            placeholderId.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            updateButton.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}