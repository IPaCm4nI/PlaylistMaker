package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ViewHolderListSongs(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.artworkUrl100)



    fun bind(song: Track) {
        trackName.text = song.trackName
        artistName.text = song.artistName
        trackTime.text = song.trackTime

        Glide.with(itemView.context)
            .load(song.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .into(artworkUrl100)
    }
}