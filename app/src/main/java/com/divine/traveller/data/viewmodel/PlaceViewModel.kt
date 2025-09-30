package com.divine.traveller.data.viewmodel

import androidx.lifecycle.ViewModel
import com.divine.traveller.data.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
) : ViewModel() {

    suspend fun getPlace(id: String) = placeRepository.getPlace(id)
}