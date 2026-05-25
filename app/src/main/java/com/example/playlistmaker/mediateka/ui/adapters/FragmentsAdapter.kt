package com.example.playlistmaker.mediateka.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.mediateka.ui.fragment.FavouriteFragment
import com.example.playlistmaker.mediateka.ui.fragment.PlaylistsFragment

class FragmentsAdapter(
    fragment: Fragment
): FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FavouriteFragment.newInstance() else PlaylistsFragment.newInstance()
    }

    override fun getItemCount(): Int {
        return 2
    }
}