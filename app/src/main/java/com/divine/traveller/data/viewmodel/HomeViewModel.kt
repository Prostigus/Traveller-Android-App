package com.divine.traveller.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.mapper.toDomainModel
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.repository.TripRepository
import com.divine.traveller.model.TripModel
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.iakovlev.timeshape.TimeZoneEngine
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TripRepository,
    val placesClient: PlacesClient,
    private val timeZoneEngineDeferred: Deferred<TimeZoneEngine>
) : ViewModel() {

    suspend fun getTimeZoneEngine(): TimeZoneEngine = timeZoneEngineDeferred.await()

    // Expose all trips as a StateFlow
    val allTrips: StateFlow<List<TripModel>> = repository.getAllTrips()
        .map { tripEntities -> tripEntities.map { it.toDomainModel() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val upcomingTrips: StateFlow<List<TripModel>> = repository.getUpcomingTrips()
        .map { tripEntities -> tripEntities.map { it.toDomainModel() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val completedTrips: StateFlow<List<TripModel>> = repository.getCompletedTrips()
        .map { tripEntities -> tripEntities.map { it.toDomainModel() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentTrips: StateFlow<List<TripModel>> = repository.getCurrentTrips()
        .map { tripEntities -> tripEntities.map { it.toDomainModel() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    suspend fun getTripById(tripId: Long): TripModel? {
        return repository.getTripById(tripId)?.toDomainModel()
    }

    fun updateTrip(tripModel: TripModel) {
        viewModelScope.launch {
            repository.updateTrip(tripModel.toEntity())
        }
    }

    // Function to insert a new trip
    fun addTrip(tripModel: TripModel) {
        viewModelScope.launch {
            repository.insertTrip(tripModel.toEntity())
        }
    }

    // Function to delete a trip
    fun deleteTrip(tripModel: TripModel) {
        viewModelScope.launch {
            repository.deleteTrip(tripModel.toEntity())
        }
    }

    // Function to mark a trip as completed
    fun markTripCompleted(tripId: Long) {
        viewModelScope.launch {
            repository.markTripCompleted(tripId)
        }
    }
}