package com.example.playlistmaker.playlist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragment.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.utils.debounce
import com.example.playlistmaker.utils.toTracksCountString
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<View>
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

        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareButton.setOnClickListener { doShare() }

        binding.editButton.setOnClickListener {
            viewModel.observerPlaylistLiveData.value?.let { playlist ->
                binding.menuPlaylistTitle.text = playlist.namePlaylist
                binding.menuPlaylistCount.text = playlist.countTracks.toTracksCountString()
                Glide.with(requireContext())
                    .load(if (playlist.pathToImage.isNullOrEmpty()) null else File(playlist.pathToImage))
                    .placeholder(R.drawable.placeholder)
                    .into(binding.menuPlaylistImage)
            }
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.menuShareItem.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            doShare()
        }

        binding.menuDeleteItem.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val playlistName = viewModel.observerPlaylistLiveData.value?.namePlaylist ?: ""
            MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialAlertDialog)
                .setTitle(getString(R.string.delete_playlist))
                .setMessage("${getString(R.string.delete_playlist_message)} «$playlistName»?")
                .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(getString(R.string.yes)) { _, _ -> viewModel.deletePlaylist() }
                .show()
        }

        viewModel.playlistDeletedLiveData.observe(viewLifecycleOwner) { deleted ->
            if (deleted) findNavController().navigateUp()
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
            binding.countTracks.text = playlist.countTracks.toTracksCountString()

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

    private fun doShare() {
        val tracks = viewModel.observerTracksLiveData.value ?: emptyList()
        if (tracks.isEmpty()) {
            Toast.makeText(requireContext(), R.string.share_playlist_empty, Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.sharePlaylist(getString(R.string.title_share))
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
