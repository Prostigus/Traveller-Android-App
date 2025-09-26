package com.divine.traveller.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.mapper.toDomainModel
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.model.TripModel
import com.divine.traveller.data.repository.PlaceRepository
import com.divine.traveller.data.repository.TripRepository
import com.divine.traveller.data.statemodel.NewTripState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.iakovlev.timeshape.TimeZoneEngine
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TripRepository,
    val placeRepository: PlaceRepository,
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

    fun createNewTrip(state: NewTripState) {
//        val startDateTime = state.startDateTime?.toLocalDate()?.atStartOfDay(ZoneId.of(state.destinationZoneIdString))
        val endDateTime = state.endDateTime?.toLocalDate()?.atTime(23, 59, 59, 999_000_000)
            ?.atZone(ZoneId.of(state.destinationZoneIdString))
        val tripModel = TripModel(
            name = state.tripName,
            destination = state.destination,
            description = state.description.takeIf { it.isNotBlank() },
            budget = null,
            startDateTime = state.startDateTime!!,
            endDateTime = endDateTime!!,
            destinationZoneIdString = state.destinationZoneIdString
        )
        addTrip(tripModel)
    }
}