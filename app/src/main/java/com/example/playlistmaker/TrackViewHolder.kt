package com.example.playlistmaker

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.song, parent, false)) {
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTimeMillis: TextView = itemView.findViewById(R.id.trackTimeMillis)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.artworkUrl100)



    fun bind(song: Track) {
        trackName.text = song.trackName
        artistName.text = song.artistName
        trackTimeMillis.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(song.trackTimeMillis)

        Glide.with(itemView.context)
            .load(song.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        2f,
                        itemView.context.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(artworkUrl100)
    }
}