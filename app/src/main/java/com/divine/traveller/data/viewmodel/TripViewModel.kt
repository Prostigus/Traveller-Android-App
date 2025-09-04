package com.divine.traveller.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.mapper.toDomainModel
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.repository.TripRepository
import com.divine.traveller.model.Trip
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val repository: TripRepository,
    val placesClient: PlacesClient
) : ViewModel() {

    // Expose all trips as a StateFlow
    val allTrips: StateFlow<List<Trip>> = repository.getAllTrips()
        .map { tripEntities -> tripEntities.map { it.toDomainModel() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Function to insert a new trip
    fun addTrip(trip: Trip) {
        viewModelScope.launch {
            repository.insertTrip(trip.toEntity())
        }
    }

    // Function to delete a trip
    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip.toEntity())
        }
    }

    // Function to mark a trip as completed
    fun markTripCompleted(tripId: Long) {
        viewModelScope.launch {
            repository.markTripCompleted(tripId)
        }
    }
}