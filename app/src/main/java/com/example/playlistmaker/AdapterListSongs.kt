package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterListSongs(
    private val listSongs: ArrayList<Track>
) : RecyclerView.Adapter<ViewHolderListSongs> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderListSongs {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song, parent, false)
        return ViewHolderListSongs(view)
    }

    override fun onBindViewHolder(holder: ViewHolderListSongs, position: Int) {
        holder.bind(listSongs[position])
    }

    override fun getItemCount(): Int {
        return listSongs.size
    }
}