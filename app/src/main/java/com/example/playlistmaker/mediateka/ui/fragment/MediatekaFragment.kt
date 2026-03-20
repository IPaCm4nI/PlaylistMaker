package com.example.playlistmaker.mediateka.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediatekaBinding
import com.example.playlistmaker.mediateka.ui.adapters.FragmentsAdapter
import com.google.android.material.tabs.TabLayoutMediator


class MediatekaFragment: Fragment() {
    private lateinit var binding: FragmentMediatekaBinding
    private lateinit var tabLayout: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediatekaBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FragmentsAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        tabLayout = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            when(pos) {
                0 -> tab.text = getString(R.string.favourite_track)
                1 -> tab.text = getString(R.string.playlists)
            }

        }
        tabLayout.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        tabLayout.detach()
    }
}