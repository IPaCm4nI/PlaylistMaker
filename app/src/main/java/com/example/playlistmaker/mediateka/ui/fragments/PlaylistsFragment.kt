package com.example.playlistmaker.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistsFragmentBinding
import com.example.playlistmaker.mediateka.ui.models.PlaylistsState
import com.example.playlistmaker.mediateka.ui.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistsFragment: Fragment() {
    private lateinit var binding: PlaylistsFragmentBinding

    private val viewModelPlaylists by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PlaylistsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelPlaylists.observerPlaylistsState().observe(viewLifecycleOwner) {
            when(it) {
                is PlaylistsState.Empty -> showEmpty()
            }
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