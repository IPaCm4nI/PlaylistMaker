package com.example.playlistmaker.player.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState
import com.example.playlistmaker.player.ui.models.TrackInPlaylistState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.ui.fragment.CreatePlaylistAdapter
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class PlayerFragment: Fragment() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

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
    private val playlistAdapter = CreatePlaylistAdapter {
        viewModel.checkExistsTrack(it, track)
    }

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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(
            bottomCallback()
        )

        with(binding) {
            recyclerPlaylistBottomSheet.adapter = playlistAdapter
            recyclerPlaylistBottomSheet.layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.checkFavourite(track.trackId)

        binding.addButton.setOnClickListener {
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.createNew.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            findNavController().navigate(
                R.id.action_playerFragment_to_playlistFragment
            )
        }

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

        viewModel.observerPlaylistsState().observe(viewLifecycleOwner) {
            when(it) {
                is PlaylistsState.Empty -> showEmpty()
                is PlaylistsState.Content -> showContent(it.playlists)
            }
        }

        viewModel.observerExistsTrack().observe(viewLifecycleOwner) { state ->
            when (state) {
                is TrackInPlaylistState.Exists -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_track_already_exists, state.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is TrackInPlaylistState.NotExists -> {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_track_added, state.playlistName),
                        Toast.LENGTH_SHORT
                    ).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
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

    private fun bottomCallback(): BottomSheetBehavior.BottomSheetCallback {
        return object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }
            override fun onSlide(p0: View, p1: Float) {}
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistAdapter.updateItem(playlists.toMutableList())

        binding.recyclerPlaylistBottomSheet.isVisible = true
    }

    private fun showEmpty() {
        binding.recyclerPlaylistBottomSheet.isVisible = false
    }

    companion object {
        private const val KEY_TRACK = "TRACK"

        fun createArgs(track: Track): Bundle = bundleOf(
                KEY_TRACK to track
            )
    }
}