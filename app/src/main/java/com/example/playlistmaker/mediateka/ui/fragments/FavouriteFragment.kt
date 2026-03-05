package com.example.playlistmaker.mediateka.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FavouriteFragmentBinding
import com.example.playlistmaker.mediateka.ui.models.FavouriteState
import com.example.playlistmaker.mediateka.ui.view_model.FavouriteViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteFragment: Fragment() {
    private lateinit var binding: FavouriteFragmentBinding

    private val viewModelFavourite by viewModel<FavouriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FavouriteFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFavourite.observerFavouriteState().observe(viewLifecycleOwner) {
            when(it) {
                is FavouriteState.Empty -> showEmpty()
            }
        }
    }

    private fun showEmpty() {
        binding.apply {
            placeholderMessage.text = getString(R.string.empty_mediateka)
            placeholderImage.setImageResource(R.drawable.not_found)
            placeholderLayout.isVisible = true
        }
    }

    companion object {
        fun newInstance() = FavouriteFragment().apply {}
    }
}