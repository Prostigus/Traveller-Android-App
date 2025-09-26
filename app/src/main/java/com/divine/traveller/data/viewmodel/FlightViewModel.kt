package com.divine.traveller.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.entity.FlightStatus
import com.divine.traveller.data.entity.TripEntity
import com.divine.traveller.data.mapper.toDomainModel
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.model.FlightModel
import com.divine.traveller.data.repository.AirportRepository
import com.divine.traveller.data.repository.FlightRepository
import com.divine.traveller.data.repository.ItineraryItemRepository
import com.divine.traveller.data.repository.PlaceRepository
import com.divine.traveller.data.repository.TripRepository
import com.divine.traveller.data.statemodel.NewFlightState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlightViewModel @Inject constructor(
    private val repository: FlightRepository,
    private val tripRepository: TripRepository,
    private val itineraryItemRepository: ItineraryItemRepository,
    val placeRepository: PlaceRepository,
    private val airportRepository: AirportRepository,
) : ViewModel() {
    private val _trip = MutableStateFlow<TripEntity?>(null)
    val trip: StateFlow<TripEntity?> = _trip.asStateFlow()

    private val _newFlightCreation =
        MutableStateFlow<NewFlightCreationState>(NewFlightCreationState.Idle)
    val newFlightCreation: StateFlow<NewFlightCreationState> = _newFlightCreation.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val flightItems: StateFlow<List<FlightModel>> = _trip
        .filterNotNull()
        .flatMapLatest { trip ->
            repository.getByTripId(trip.id)
                .map { entities ->
                    entities.map { it.toDomainModel() }
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun loadItems(tripId: Long) {
        viewModelScope.launch {
            _trip.value = tripRepository.getTripByIdCached(tripId)
        }
    }

    fun createNewFlight(tripId: Long, state: NewFlightState) {
        viewModelScope.launch {
            try {
                _newFlightCreation.value = NewFlightCreationState.Creating
                Log.d("FlightViewModel", "Creating new flight with state: $state")
                val departureModel = state.departurePlace?.location?.let { latLng ->
                    airportRepository.findNearestIata(
                        lat = latLng.latitude,
                        lng = latLng.longitude
                    )?.toDomainModel()
                }
                val arrivalModel = state.arrivalPlace?.location?.let { latLng ->
                    airportRepository.findNearestIata(
                        lat = latLng.latitude,
                        lng = latLng.longitude
                    )?.toDomainModel()
                }

                val newFlight = FlightModel(
                    id = state.id,
                    tripId = tripId,
                    airline = state.airline,
                    flightNumber = state.flightNumber,
                    departureAirport = departureModel,
                    arrivalAirport = arrivalModel,
                    departurePlaceId = state.departurePlace?.id,
                    arrivalPlaceId = state.arrivalPlace?.id,
                    departureDateTime = state.departureDateTime!!,
                    arrivalDateTime = state.arrivalDateTime!!,
                    status = FlightStatus.SCHEDULED
                )
                repository.insertFlightWithItinerary(newFlight) {
                    _newFlightCreation.value = NewFlightCreationState.Success
                }
            } catch (e: Exception) {
                Log.e("FlightViewModel", "Error in createNewFlight", e)
                _newFlightCreation.value = NewFlightCreationState.Error(e.message)
            }
        }
    }

    fun resetNewFlightCreation() {
        _newFlightCreation.value = NewFlightCreationState.Idle
    }

    fun delete(flight: FlightModel, onComplete: () -> Unit = {}) = viewModelScope.launch {
        repository.delete(flight.toEntity())
    }

    suspend fun getById(id: Long): FlightModel? {
        return repository.getById(id)?.toDomainModel()
    }
}

sealed class NewFlightCreationState {
    data object Idle : NewFlightCreationState()
    data object Creating : NewFlightCreationState()
    data object Success : NewFlightCreationState()
    data class Error(val message: String? = null) : NewFlightCreationState()
}