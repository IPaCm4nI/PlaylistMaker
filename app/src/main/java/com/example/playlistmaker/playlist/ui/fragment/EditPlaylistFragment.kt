package com.example.playlistmaker.playlist.ui.fragment

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.getValue

class EditPlaylistFragment: CreatePlaylistFragment() {
    override val viewModel by viewModel<EditPlaylistViewModel>()

    private val playlist: Playlist by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(KEY_PLAYLIST, Playlist::class.java)
                ?: error("Playlist not found")
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(KEY_PLAYLIST)
                ?: error("Playlist not found")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titlePlaylist.text = getString(R.string.titleEditPlaylist)
        binding.createButton.text = getString(R.string.saveEditPlaylist)

        viewModel.playlistLiveData.observe(viewLifecycleOwner) { playlist ->
            binding.enterTitle.setText(playlist.namePlaylist)
            binding.enterDescription.setText(playlist.descriptionPlaylist)
            binding.createButton.isEnabled = playlist.namePlaylist.isNotBlank()

            binding.defaultImage.isVisible = playlist.pathToImage.isNullOrEmpty()
            if (!playlist.pathToImage.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(File(playlist.pathToImage))
                    .placeholder(R.drawable.placeholder)
                    .transform(
                        RoundedCorners(
                            TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                2f,
                                resources.displayMetrics
                            ).toInt()
                        )
                    )
                    .into(binding.selectedImage)
            }
        }

        viewModel.initPlaylist(playlist)
    }

    override fun onBackPressed() {
        findNavController().navigateUp()
    }

    override fun onCreateButtonClicked() {
        val name = binding.enterTitle.text.toString()
        if (name.isBlank()) return

        val savedPath = selectedImage?.let { saveImageToApp(it) } ?: playlist.pathToImage
        viewModel.updatePlaylist(
            playlist.copy(
                namePlaylist = name,
                descriptionPlaylist = binding.enterDescription.text.toString(),
                pathToImage = savedPath
            )
        ) {
            findNavController().navigateUp()
        }
    }

    companion object {
        private const val KEY_PLAYLIST = "PLAYLIST"

        fun createArgs(playlist: Playlist): Bundle = bundleOf(
            KEY_PLAYLIST to playlist
        )
    }
}