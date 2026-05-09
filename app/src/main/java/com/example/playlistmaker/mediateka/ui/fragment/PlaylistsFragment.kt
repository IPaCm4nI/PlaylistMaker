package com.example.playlistmaker.mediateka.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState
import com.example.playlistmaker.mediateka.ui.view_model.PlaylistsViewModel
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.ui.fragment.PlaylistFragment
import com.example.playlistmaker.search.ui.fragment.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistsFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistsBinding

    private val viewModelPlaylists by viewModel<PlaylistsViewModel>()
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter()

        binding.recyclerPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerPlaylists.adapter = adapter

        viewModelPlaylists.observerPlaylistsState().observe(viewLifecycleOwner) {
            when(it) {
                is PlaylistsState.Empty -> showEmpty()
                is PlaylistsState.Content -> showContent(it.playlists)
            }
        }

        binding.createNew.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediatekaFragment_to_playlistFragment
            )
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.apply {
            placeholderLayout.isVisible = false
            recyclerPlaylists.isVisible = true
            adapter.updateItem(playlists.toMutableList())
        }
    }

    private fun showEmpty() {
        binding.apply {
            placeholderMessage.text = getString(R.string.empty_playlists)
            placeholderImage.setImageResource(R.drawable.not_found)
            placeholderLayout.isVisible = true
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment().apply {}
    }
}