package com.example.playlistmaker.mediateka.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.playlist.domain.models.Playlist

class PlaylistAdapter(
    val clickListener: playlistClickListener
): RecyclerView.Adapter<PlaylistViewHolder> () {

    var listPlaylists: MutableList<Playlist> = mutableListOf()

    fun updateItem(newItems: MutableList<Playlist>) {
        val oldItems = listPlaylists

        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(object: DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldItems.size
            }

            override fun getNewListSize(): Int {
                return newItems.size
            }

            override fun areItemsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                return oldItems[oldItemPosition].id == newItems[newItemPosition].id
            }

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }
        })

        listPlaylists = newItems.toMutableList()
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(listPlaylists[position])
        holder.itemView.setOnClickListener {
            clickListener.onPlaylistClick(listPlaylists[position])
        }
    }

    override fun getItemCount(): Int {
        return listPlaylists.size
    }

    fun interface playlistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}