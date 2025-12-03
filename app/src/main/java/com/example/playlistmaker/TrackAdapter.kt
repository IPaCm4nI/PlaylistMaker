package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(val clickListener: trackClickListener): RecyclerView.Adapter<TrackViewHolder> () {

    var listSongs: MutableList<Track> = mutableListOf()

    fun updateItem(newItems: MutableList<Track>) {
        val oldItems = listSongs

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

        listSongs = newItems.toMutableList()
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(listSongs[position])

        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(listSongs[position])
        }
    }

    override fun getItemCount(): Int {
        return listSongs.size
    }

    fun interface trackClickListener {
        fun onTrackClick(track: Track)
    }
}