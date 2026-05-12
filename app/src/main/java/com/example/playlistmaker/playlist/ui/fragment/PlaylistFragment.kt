package com.example.playlistmaker.playlist.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class PlaylistFragment: Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!
    private var selectedImage: Uri? = null
    private val pickerImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.selectedImage.setImageURI(uri)
            selectedImage = uri
            binding.defaultImage.isVisible = false
        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showDialog()
        }
    }

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialAlertDialog)
            .setTitle(requireContext().getString(R.string.title_dialog_exit))
            .setMessage(requireContext().getString(R.string.message_dialog_exit))
            .setNeutralButton(requireContext().getString(R.string.cancel_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(requireContext().getString(R.string.ok)) { _, _ ->
                findNavController().navigateUp()
            }

        binding.backArrow.setOnClickListener {
            showDialog()
        }

        binding.enterTitle.addTextChangedListener{
            binding.createButton.isEnabled = it?.isEmpty() != true
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.placePicture.setOnClickListener {
            pickerImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createButton.setOnClickListener {
            val savedPath = selectedImage?.let { saveImageToApp(it) } ?: ""
            val name = binding.enterTitle.text.toString()

            viewModel.createPlaylist(
                Playlist(
                    namePlaylist = name,
                    descriptionPlaylist = binding.enterDescription.text.toString(),
                    pathToImage = savedPath,
                    tracksInPlaylist = "",
                    countTracks = 0
                )
            )

            Toast
                .makeText(requireContext(), getString(R.string.toast_playlist_created, name), Toast.LENGTH_LONG)
                .show()

            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog() {
        if (selectedImage != null
            || binding.enterTitle.text?.isNotEmpty() == true
            || binding.enterDescription.text?.isNotEmpty() == true) {
            confirmDialog.show()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun saveImageToApp(uri: Uri): String {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playmaker")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val fileImage = File(filePath, "${System.currentTimeMillis()}.jpg")

        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(fileImage)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return fileImage.absolutePath
    }
}