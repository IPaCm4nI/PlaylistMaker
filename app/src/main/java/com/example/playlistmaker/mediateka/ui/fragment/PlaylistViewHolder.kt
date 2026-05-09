package com.example.playlistmaker.mediateka.ui.fragment

import android.os.Environment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class PlaylistViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.playlist, parent, false)) {

    private val imagePlaylist: ImageView = itemView.findViewById(R.id.imagePlaylist)
    private val titlePlaylist: TextView =itemView.findViewById(R.id.titlePlaylist)
    private val countTracks: TextView = itemView.findViewById(R.id.countTracks)

    fun bind(playlist: Playlist) {
        titlePlaylist.text = playlist.namePlaylist
        countTracks.text = playlist.countTracks.toString()

        val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playmaker")
        val file = File(filePath, "${playlist.namePlaylist}.jpg")

        Glide.with(itemView.context)
            .load(file)
            .placeholder(R.drawable.placeholder)
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        8f,
                        itemView.context.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(imagePlaylist)
    }
}