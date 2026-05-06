package com.example.playlistmaker.mediateka.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouriteBinding
import com.example.playlistmaker.mediateka.ui.models.FavouriteState
import com.example.playlistmaker.mediateka.ui.view_model.FavouriteViewModel
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragment.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteFragment: Fragment() {
    private lateinit var binding: FragmentFavouriteBinding

    private val viewModelFavourite by viewModel<FavouriteViewModel>()

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter { track ->
            findNavController().navigate(
                R.id.action_mediatekaFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModelFavourite.observerFavouriteState().observe(viewLifecycleOwner) {
            when(it) {
                is FavouriteState.Empty -> showEmpty()
                is FavouriteState.Content -> showContent(it.favouriteTracks)
            }
        }
    }

    private fun showEmpty() {
        binding.apply {
            placeholderMessage.text = getString(R.string.empty_mediateka)
            placeholderImage.setImageResource(R.drawable.not_found)
            placeholderLayout.isVisible = true
            recyclerView.isVisible = false
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.placeholderLayout.isVisible = false
        binding.recyclerView.isVisible = true
        adapter.updateItem(tracks.toMutableList())
    }

    companion object {
        fun newInstance() = FavouriteFragment().apply {}
    }
}