package com.example.playlistmaker.player.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class PlayerFragment: Fragment() {
    private val track: Track by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(KEY_TRACK, Track::class.java)
                ?: error("Track not found")
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(KEY_TRACK)
                ?: error("Track not found")
        }
    }
    private val viewModel by viewModel<PlayerViewModel> { parametersOf(track.previewUrl) }
    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkFavourite(track.trackId)

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.onFavouriteClicked(track)
        }

        viewModel.preparePlayer()

        viewModel.observerPlayerState().observe(viewLifecycleOwner) {
            binding.playButton.isSelected = !it.isPlayButton
            binding.currentTime.text = it.progress
        }

        viewModel.observerIsFavourite().observe(viewLifecycleOwner) { isFavourite ->
            binding.favoriteButton.isSelected = isFavourite
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
            .into(binding.ivAlbum)

        binding.tvNameSong.text = track.trackName
        binding.tvNameGroup.text = track.artistName
        binding.time.text = track.trackTimeMillis.trim()

        if (track.collectionName?.isNotEmpty() == true) {
            binding.groupAlbum.isVisible = true
            binding.album.text = track.collectionName
        } else {
            binding.groupAlbum.isVisible = false
        }

        if (track.releaseDate?.isNotEmpty() == true) {
            binding.groupYear.isVisible = true
            binding.year.text = track.releaseDate?.substringBefore("-")
        } else {
            binding.groupYear.isVisible = false
        }

        if (track.primaryGenreName?.isNotEmpty() == true) {
            binding.groupGenre.isVisible = true
            binding.genre.text = track.primaryGenreName
        } else {
            binding.groupGenre.isVisible = false
        }

        if (track.country?.isNotEmpty() == true) {
            binding.groupCountry.isVisible = true
            binding.country.text = track.country
        } else {
            binding.groupCountry.isVisible = false
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        private const val KEY_TRACK = "TRACK"

        fun createArgs(track: Track): Bundle = bundleOf(
                KEY_TRACK to track
            )
    }
}