package com.example.playlistmaker.playlist.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.fragment.TrackViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BottomSheetTrackAdapter(
    val clickListener: trackClickListener,
    val longClickListener: trackLongClickListener
): RecyclerView.Adapter<TrackViewHolder>() {

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
                return oldItems[oldItemPosition].trackId == newItems[newItemPosition].trackId
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
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) clickListener.onTrackClick(listSongs[pos])
        }

        holder.itemView.setOnLongClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnLongClickListener true
            val track = listSongs[pos]
            MaterialAlertDialogBuilder(holder.itemView.context, R.style.CustomMaterialAlertDialog)
                .setTitle(holder.itemView.context.getString(R.string.title_dialog_delete))
                .setNegativeButton(holder.itemView.context.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(holder.itemView.context.getString(R.string.yes)) { _, _ ->
                    longClickListener.onTrackLongClick(track)
                }
                .show()
            true
        }
    }

    override fun getItemCount(): Int {
        return listSongs.size
    }

    fun interface trackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface trackLongClickListener {
        fun onTrackLongClick(track: Track)
    }
}