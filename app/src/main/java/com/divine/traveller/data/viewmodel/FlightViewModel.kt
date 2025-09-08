package com.divine.traveller.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val placesClient: PlacesClient
) : ViewModel() {

    private val _trip = MutableStateFlow<TripEntity?>(null)
    val trip: StateFlow<TripEntity?> = _trip.asStateFlow()


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

    fun insert(flight: FlightModel, onComplete: (Long) -> Unit = {}) = viewModelScope.launch {
        val id = repository.insert(flight.toEntity())
        val all = itineraryItemRepository.getAllsuspend()
        Log.d("DBG", "DB instance repo=${itineraryItemRepository.hashCode()} allItems=$all")
        val itemsForFlight = itineraryItemRepository.getItineraryItemsForFlight(id)
        Log.d("DBG", "itemsForFlight for $id = $itemsForFlight")
        val newItineraryItem = ItineraryItemModel(
            id = if (itemsForFlight.isNotEmpty()) itemsForFlight[0].id else 0,
            title = "Flight from ${flight.departureAirport} to ${flight.arrivalAirport}",
            tripId = flight.tripId,
            category = ItineraryCategory.FLIGHT,
            status = ItineraryItemStatus.NONE,
            startDateTime = flight.departureDate,
            endDateTime = flight.arrivalDate,
            flightId = id
        )
        if (itemsForFlight.isNotEmpty()) {
            itineraryItemRepository.update(newItineraryItem.toEntity())
        } else {
            itineraryItemRepository.insert(newItineraryItem.toEntity())
        }
        onComplete(id)
    }

//    fun update(flight: FlightModel, onComplete: (Long) -> Unit = {}) = viewModelScope.launch {
//        repository.update(flight.toEntity())
//    }

    fun delete(flight: FlightModel, onComplete: () -> Unit = {}) = viewModelScope.launch {
        repository.delete(flight.toEntity())
    }

    suspend fun getById(id: Long): FlightModel? {
        return repository.getById(id)?.toDomainModel()
    }
}