package com.example.playlistmaker.mediateka.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediateka.ui.models.FavouriteState

class FavouriteViewModel: ViewModel() {
    private val mutableFavouriteState = MutableLiveData<FavouriteState>()
    fun observerFavouriteState(): LiveData<FavouriteState> = mutableFavouriteState

    // TODO Временная заглушка решил сделать так
    init {
        mutableFavouriteState.postValue(
            FavouriteState.Empty
        )
    }
}