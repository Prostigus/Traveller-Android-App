package com.divine.traveller.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.entity.TripEntity
import com.divine.traveller.data.entity.endAsLocalDate
import com.divine.traveller.data.entity.startAsLocalDate
import com.divine.traveller.data.mapper.toDomainModel
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.model.startAsLocalDate
import com.divine.traveller.data.repository.ItineraryItemRepository
import com.divine.traveller.data.repository.TripRepository
import com.divine.traveller.util.toZoneId
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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    private val repository: ItineraryItemRepository,
    private val tripRepository: TripRepository,
    val placesClient: PlacesClient
) : ViewModel() {

    private val _trip = MutableStateFlow<TripEntity?>(null)
    val trip: StateFlow<TripEntity?> = _trip.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val itemsByDay: StateFlow<List<Pair<LocalDate, List<ItineraryItemModel>>>> = _trip
        .filterNotNull()
        .flatMapLatest { trip ->
            repository.getByTripId(trip.id)
                .map { entities ->
                    val items = entities.map { it.toDomainModel() }

                    val zoneId = toZoneId(trip.destinationZoneIdString)
                    val start = trip.startAsLocalDate(zoneId)
                    val end = trip.endAsLocalDate(zoneId)
                    val allDays = generateSequence(start) { it.plusDays(1) }
                        .takeWhile { !it.isAfter(end) }
                        .toList()

                    val itemsGrouped = items.groupBy {
                        it.startAsLocalDate(zoneId)
                    }
                    allDays.map { day ->
                        day to (itemsGrouped[day]?.sortedBy { it.startDateTime } ?: emptyList())
                    }
                }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadItems(tripId: Long) {
        viewModelScope.launch {
            _trip.value = tripRepository.getTripByIdCached(tripId)
        }
    }

    fun insert(item: ItineraryItemModel, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insert(item.toEntity())
            onComplete(id)
        }
    }

    fun update(item: ItineraryItemModel) {
        viewModelScope.launch {
            repository.update(item.toEntity())
        }
    }

    fun delete(item: ItineraryItemModel) {
        viewModelScope.launch {
            repository.delete(item.toEntity())
        }
    }

    suspend fun getById(id: Long): ItineraryItemModel? {
        return repository.getById(id)?.toDomainModel()
    }
}