package com.example.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.SongsState
import com.example.playlistmaker.search.ui.view_model.SongsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {
    private var searchText: String = SEARCH_DEF
    private lateinit var binding: FragmentSearchBinding
    private val handler = Handler(Looper.getMainLooper())
    private var textWatcher: TextWatcher? = null
    private var isClickAllowed = true
    private val viewModel by viewModel<SongsViewModel>()
    private var searchAdapter = TrackAdapter {
        viewModel.saveToHistory(it)

        moveToPlayer(it)
    }
    private var historyAdapter = TrackAdapter {
        viewModel.saveToHistory(it)

        moveToPlayer(it)
    }

    private enum class Error {
        NOT_FOUND,
        NOT_CONNECTION
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchText = savedInstanceState?.getString(
            KEY_SEARCH,
            SEARCH_DEF
        ) ?: ""

        binding.search.setText(searchText)

        if(searchText.isNotEmpty()) {
            binding.search.setSelection(searchText.length)
            viewModel.searchDebounce(searchText)
        }

        binding.listSongs.adapter = searchAdapter
        binding.listHistory.adapter = historyAdapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        textWatcher = binding.search.doOnTextChanged { text, _, _, _ ->
            searchText = text?.toString() ?: ""

            binding.clearIcon.isVisible = !text.isNullOrEmpty()

            if (text.isNullOrEmpty() && binding.search.hasFocus()) {
                showHistory()
            } else {
                viewModel.searchDebounce(text?.toString() ?: "")
                showRecyclerSongs()
            }
        }

         binding.search.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.search.text.isNullOrEmpty()) {
                showHistory()
            }
            else {
                showRecyclerSongs()
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.search.setText("")
            binding.search.requestFocus()

            showHistory()

            hideKeyboard()
        }

        binding.bUpdate.setOnClickListener {
            if (searchText.isNotEmpty()) {
                viewModel.searchNow(searchText)
            }
        }

        binding.bClearHistory.setOnClickListener {
            viewModel.clearHistory()
            binding.llHistory.isVisible = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH, searchText)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        textWatcher?.let { binding.search.removeTextChangedListener(it) }
    }

    private fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showPage(text: String) {
        when(text) {
            Error.NOT_FOUND.name.lowercase() -> {
                binding.placeholderImage.setImageResource(R.drawable.not_found)
                binding.placeholderMessage.text = getString(R.string.nothing_search)
            }
            Error.NOT_CONNECTION.name.lowercase() -> {
                binding.placeholderImage.setImageResource(R.drawable.not_connection)
                binding.placeholderMessage.text = getString(R.string.trouble_network)
                binding.bUpdate.isVisible = true
            }
        }

        binding.placeholderLayout.isVisible = true
        binding.listSongs.isVisible = false
        binding.progressBar.isVisible = false
        binding.llHistory.isVisible = false
    }

    private fun showHistory() {
        viewModel.getHistory()
    }

    private fun showResults(songs: List<Track>) {
        searchAdapter.updateItem(songs.toMutableList())

        binding.progressBar.isVisible = false
        binding.listSongs.isVisible = true
        binding.llHistory.isVisible = false
        binding.placeholderLayout.isVisible = false
        binding.bUpdate.isVisible = false
    }

    private fun showRecyclerSongs() {
        binding.listSongs.isVisible = true
        binding.llHistory.isVisible = false
        binding.placeholderLayout.isVisible = false
        binding.bUpdate.isVisible = false
    }

    private fun showLoading() {
        binding.listSongs.isVisible = false
        binding.llHistory .isVisible = false
        binding.placeholderLayout.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showHistory(tracks: List<Track>) {
        historyAdapter.updateItem(tracks.toMutableList())
        binding.llHistory.isVisible = tracks.isNotEmpty()

        binding.listSongs.isVisible = false
        binding.placeholderLayout.isVisible = false
        binding.progressBar.isVisible = false
        binding.bUpdate.isVisible = false
    }

    private fun moveToPlayer(track: Track) {
        if (clickDebounce()) {
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
                )
        }
    }

    private fun render(state: SongsState) {
        when(state) {
            is SongsState.Loading -> showLoading()
            is SongsState.Error -> showPage(state.errorMessage)
            is SongsState.Empty -> showPage(state.message)
            is SongsState.Content -> {
                if (state.isHistory) {
                    showHistory(state.songs)
                } else {
                    showResults(state.songs)
                }
            }
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    companion object {
        const val KEY_SEARCH = "SEARCH"
        const val SEARCH_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}