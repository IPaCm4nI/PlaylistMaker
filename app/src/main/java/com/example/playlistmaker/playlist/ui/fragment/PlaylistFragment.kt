package com.example.playlistmaker.playlist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragment.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel> {
        parametersOf(requireArguments().getInt(KEY_PLAYLIST))
    }

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var bottomSheetAdapter = BottomSheetTrackAdapter(
        clickListener = { onTrackClickDebounce(it) },
        longClickListener = { track -> viewModel.deleteTrack(track) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        binding.recyclerBottomSheet.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBottomSheet.adapter = bottomSheetAdapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.skipCollapsed = false
        bottomSheetBehavior.addBottomSheetCallback(bottomCallback())
        binding.root.doOnLayout {
            val marginTop = resources.getDimensionPixelSize(R.dimen.dp_24)
            bottomSheetBehavior.peekHeight =
                binding.root.height - binding.shareButton.bottom - marginTop
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.observerCountMinuteTracksLiveData.observe(viewLifecycleOwner) { countMinute ->
            binding.countMinuteTracks.text = getMinutesText(countMinute)
        }

        viewModel.observerTracksLiveData.observe(viewLifecycleOwner) { tracks ->
            bottomSheetAdapter.updateItem(tracks.toMutableList())
        }

        viewModel.observerPlaylistLiveData.observe(viewLifecycleOwner) { playlist ->
            binding.titlePlaylist.text = playlist.namePlaylist
            binding.descPlaylist.text = playlist.descriptionPlaylist
            binding.countTracks.text = resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.countTracks,
                playlist.countTracks
            )

            Glide.with(requireContext())
                .load(if (playlist.pathToImage.isNullOrEmpty()) null else File(playlist.pathToImage))
                .placeholder(R.drawable.placeholder)
                .into(binding.imagePlaylist)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bottomCallback(): BottomSheetBehavior.BottomSheetCallback {
        return object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.isVisible = false
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.isVisible = true
                    }
                    else -> Unit
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.isVisible = slideOffset > 0f
            }
        }
    }

    private fun getMinutesText(minutes: Int): String {
        val lastTwoDigits = minutes % 100
        val lastDigit = minutes % 10

        val word = when {
            lastTwoDigits in 11..14 -> "минут"
            lastDigit == 1 -> "минута"
            lastDigit in 2..4 -> "минуты"
            else -> "минут"
        }

        return "$minutes $word"
    }

    companion object {
        private const val KEY_PLAYLIST = "PLAYLIST"

        fun createArgs(idPlaylist: Int): Bundle = bundleOf(
            KEY_PLAYLIST to idPlaylist
        )
    }
}
