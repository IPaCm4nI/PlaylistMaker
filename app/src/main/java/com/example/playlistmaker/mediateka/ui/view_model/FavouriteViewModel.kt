package com.example.playlistmaker.mediateka.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.mediateka.domain.db.api.FavouriteTrackInteractor
import com.example.playlistmaker.mediateka.ui.models.FavouriteState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val favouriteTrackInteractor: FavouriteTrackInteractor
): ViewModel() {
    private val mutableFavouriteState = MutableLiveData<FavouriteState>()
    fun observerFavouriteState(): LiveData<FavouriteState> = mutableFavouriteState

    init {
        viewModelScope.launch {
            favouriteTrackInteractor.getTracks().collect { tracks ->
                renderState(tracks)
            }
        }
    }

    private fun renderState(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            mutableFavouriteState.postValue(FavouriteState.Empty)
        } else {
            mutableFavouriteState.postValue(FavouriteState.Content(tracks))
        }
    }
}