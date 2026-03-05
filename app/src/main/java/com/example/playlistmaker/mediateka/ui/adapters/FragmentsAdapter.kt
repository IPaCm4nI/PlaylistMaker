package com.example.playlistmaker.mediateka.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.mediateka.ui.fragments.FavouriteFragment
import com.example.playlistmaker.mediateka.ui.fragments.PlaylistsFragment

class FragmentsAdapter(
    host: FragmentActivity
): FragmentStateAdapter(host) {
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FavouriteFragment.newInstance() else PlaylistsFragment.newInstance()
    }

    override fun getItemCount(): Int {
        return 2
    }

}