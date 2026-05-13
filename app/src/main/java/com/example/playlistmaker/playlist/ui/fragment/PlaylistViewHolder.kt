package com.example.playlistmaker.playlist.ui.fragment

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.BottomSheetPlaylistsBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import java.io.File

class PlaylistViewHolder(
    private val binding: BottomSheetPlaylistsBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        Glide.with(itemView.context)
            .load(if (playlist.pathToImage.isNullOrEmpty()) null else File(playlist.pathToImage))
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
            .into(binding.imagePlaylist)

        binding.titlePlaylist.text = playlist.namePlaylist
        binding.countTracks.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.countTracks,
            playlist.countTracks
        )
    }

    companion object {
        fun from(view: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(view.context)
            val binding = BottomSheetPlaylistsBinding.inflate(inflater, view, false)

            return PlaylistViewHolder(binding)
        }
    }
}