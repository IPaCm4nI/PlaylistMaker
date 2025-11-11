package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
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

    private val listSongs: MutableList<Track> = mutableListOf()

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

        val toolbarSearchId = findViewById<MaterialToolbar>(R.id.toolbar_search)
        toolbarSearchId.setNavigationOnClickListener {
            hideKeyboard()

            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        val recyclerViewSongs = findViewById<RecyclerView>(R.id.listSongs)
//        val listSongs: List<Track> = listOf(
//            Track("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
//            Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
//            Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
//            Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
//            Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
//        )

        trackAdapter.listSongs = listSongs
        recyclerViewSongs.adapter = trackAdapter

        val editTextId = findViewById<EditText>(R.id.search)
        val clearButtonId = findViewById<ImageView>(R.id.clearIcon)
        clearButtonId.setOnClickListener {
            editTextId.setText("")
            listSongs.clear()
            hideKeyboard()
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
                val query = editTextId.text.toString().trim()

                if (query.isNotEmpty()) {
                    //TODO Send request, move to fun, added image for error
                    songService.getSongs(query)
                        .enqueue(object: Callback<SongResponse> {
                            override fun onResponse(
                                call: Call<SongResponse?>,
                                response: Response<SongResponse?>
                            ) {
                                if (response.code() == 200) {
                                    listSongs.clear()
                                    listSongs.addAll(response.body()?.results!!)
                                    trackAdapter.notifyDataSetChanged() // TODO test and move Diff class
                                } else {
                                    //TODO handle error server and get message
                                }
                            }

                            override fun onFailure(
                                call: Call<SongResponse?>,
                                t: Throwable
                            ) {
                                t.printStackTrace().toString()
                            }

                        })
                }
                true
            } else {
                false
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(KEY_SEARCH, SEARCH_DEF)

        val editTextId = findViewById<EditText>(R.id.search)
        editTextId.setText(searchText)
    }

//    private fun clearButtonVisibility(s: CharSequence?): Int {
//        return if (s.isNullOrEmpty()) {
//            View.GONE
//        } else {
//            View.VISIBLE
//        }
//    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}