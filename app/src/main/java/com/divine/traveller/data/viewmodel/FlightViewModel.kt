package com.divine.traveller.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.entity.FlightStatus
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import com.divine.traveller.data.entity.TripEntity
import com.divine.traveller.data.mapper.toDomainModel
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.model.FlightModel
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.repository.FlightRepository
import com.divine.traveller.data.repository.ItineraryItemRepository
import com.divine.traveller.data.repository.TripRepository
import com.divine.traveller.data.statemodel.NewFlightState
import com.divine.traveller.util.AirportCodeParser
import com.google.android.libraries.places.api.net.PlacesClient
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
    val placesClient: PlacesClient,
    private val airportCodeParser: AirportCodeParser,
) : ViewModel() {
    private val _trip = MutableStateFlow<TripEntity?>(null)
    val trip: StateFlow<TripEntity?> = _trip.asStateFlow()

    var newFlightCreated = MutableStateFlow(false)

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
                Log.d("FlightViewModel", "Creating new flight with state: $state")
                val departureIATA = state.departurePlace?.location?.let { latLng ->
                    airportCodeParser.getIataCode(
                        lat = latLng.latitude,
                        lng = latLng.longitude
                    ) ?: ""
                } ?: ""
                val arrivalIATA = state.arrivalPlace?.location?.let { latLng ->
                    airportCodeParser.getIataCode(
                        lat = latLng.latitude,
                        lng = latLng.longitude
                    ) ?: ""
                } ?: ""

                Log.d(
                    "FlightViewModel",
                    "Fetched IATA codes - Departure: $departureIATA, Arrival: $arrivalIATA"
                )
                val newFlight = FlightModel(
                    id = 0L,
                    tripId = tripId,
                    airline = state.airline,
                    flightNumber = state.flightNumber,
                    departureAirport = state.departurePlace?.displayName!!,
                    arrivalAirport = state.arrivalPlace?.displayName!!,
                    departureDateTime = state.departureDateTime!!,
                    arrivalDateTime = state.arrivalDateTime!!,
                    departureIATA = departureIATA,
                    arrivalIATA = arrivalIATA,
                    status = FlightStatus.SCHEDULED
                )
                insert(newFlight) {
                    newFlightCreated.value = true
                }
            } catch (e: Exception) {
                Log.e("FlightViewModel", "Error in createNewFlight", e)
            }
        }
    }

    fun insert(flight: FlightModel, onComplete: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repository.insert(flight.toEntity())
        val itemsForFlight = itineraryItemRepository.getItineraryItemsForFlight(id)
        val newItineraryItem = ItineraryItemModel(
            id = if (itemsForFlight.isNotEmpty()) itemsForFlight[0].id else 0,
            title = if (flight.departureIATA.isNotEmpty() && flight.arrivalIATA.isNotEmpty()) "Flight from ${flight.departureIATA} to ${flight.arrivalIATA}" else "Flight from ${flight.departureAirport} to ${flight.arrivalAirport}",
            tripId = flight.tripId,
            category = ItineraryCategory.FLIGHT,
            status = ItineraryItemStatus.NONE,
            startDateTime = flight.departureDateTime,
            endDateTime = flight.arrivalDateTime,
            dayDate = flight.departureDateTime.toLocalDate(),
            flightId = id
        )
        if (itemsForFlight.isNotEmpty()) {
            itineraryItemRepository.update(newItineraryItem.toEntity())
        } else {
            itineraryItemRepository.insertWithNextOrder(
                newItineraryItem.toEntity(),
                flight.departureDateTime.toLocalDate()
            )
        }
        onComplete(id)
    }

    fun delete(flight: FlightModel, onComplete: () -> Unit = {}) = viewModelScope.launch {
        repository.delete(flight.toEntity())
    }

    suspend fun getById(id: Long): FlightModel? {
        return repository.getById(id)?.toDomainModel()
    }
}