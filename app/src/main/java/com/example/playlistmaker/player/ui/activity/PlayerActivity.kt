package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {
    private lateinit var backArrow: ImageButton
    private lateinit var imageAlbum: ImageView
    private lateinit var nameSong: TextView
    private lateinit var favoriteButton: ImageButton
    private lateinit var playButton: ImageView
    private lateinit var nameGroup: TextView
    private lateinit var time: TextView
    private lateinit var groupAlbum: Group
    private lateinit var album: TextView
    private lateinit var groupYear: Group
    private lateinit var year: TextView
    private lateinit var groupGenre: Group
    private lateinit var genre: TextView
    private lateinit var groupCountry: Group
    private lateinit var country: TextView
    private lateinit var currentTime: TextView

    private val track: Track by lazy {
        Gson().fromJson(intent.getStringExtra(SearchActivity.Companion.KEY_TRACK), Track::class.java)
    }
    private val viewModel by viewModel<PlayerViewModel> { parametersOf(track.previewUrl) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backArrow = findViewById(R.id.backArrow)
        imageAlbum = findViewById(R.id.ivAlbum)
        nameSong = findViewById(R.id.tvNameSong)
        favoriteButton = findViewById(R.id.favoriteButton)
        playButton = findViewById(R.id.playButton)
        currentTime = findViewById(R.id.currentTime)
        nameGroup = findViewById(R.id.tvNameGroup)
        time = findViewById(R.id.time)
        groupAlbum = findViewById(R.id.groupAlbum)
        album = findViewById(R.id.album)
        groupYear = findViewById(R.id.groupYear)
        year = findViewById(R.id.year)
        groupGenre = findViewById(R.id.groupGenre)
        genre = findViewById(R.id.genre)
        groupCountry = findViewById(R.id.groupCountry)
        country = findViewById(R.id.country)

        backArrow.setOnClickListener {
            finish()
        }

        favoriteButton.setOnClickListener {
            favoriteButton.isSelected = !favoriteButton.isSelected
        }

        playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        viewModel.preparePlayer()

        viewModel.observerPlayerState().observe(this) {
            playButton.isSelected = !it.isPlayButton
            currentTime.text = it.progress
        }

        Glide
            .with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder512)
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        this.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(imageAlbum)

        nameSong.text = track.trackName
        nameGroup.text = track.artistName
        time.text = track.trackTimeMillis.trim()

        if (track.collectionName?.isNotEmpty() == true) {
            groupAlbum.isVisible = true
            album.text = track.collectionName
        } else {
            groupAlbum.isVisible = false
        }

        if (track.releaseDate?.isNotEmpty() == true) {
            groupYear.isVisible = true
            year.text = track.releaseDate?.substringBefore("-")
        } else {
            groupYear.isVisible = false
        }

        if (track.primaryGenreName?.isNotEmpty() == true) {
            groupGenre.isVisible = true
            genre.text = track.primaryGenreName
        } else {
            groupGenre.isVisible = false
        }

        if (track.country?.isNotEmpty() == true) {
            groupCountry.isVisible = true
            country.text = track.country
        } else {
            groupCountry.isVisible = false
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
}