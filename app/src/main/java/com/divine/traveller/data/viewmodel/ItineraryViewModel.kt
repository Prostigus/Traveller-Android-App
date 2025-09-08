package com.divine.traveller.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.mapper.toDomainModel
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.model.TripModel
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
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    private val repository: ItineraryItemRepository,
    private val tripRepository: TripRepository,
    val placesClient: PlacesClient
) : ViewModel() {

    private val _trip = MutableStateFlow<TripModel?>(null)
    val trip: StateFlow<TripModel?> = _trip.asStateFlow()

    private val _selectedDay = MutableStateFlow<LocalDate?>(null)
    val selectedDay: StateFlow<LocalDate?> = _selectedDay.asStateFlow()

    fun selectDay(day: LocalDate) {
        _selectedDay.value = day
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val itemsForDay: StateFlow<List<ItineraryItemModel>> = _trip
        .filterNotNull()
        .flatMapLatest { trip ->
            _selectedDay.filterNotNull().flatMapLatest { day ->
                repository.getItemsForDayOrdered(trip.id, day)
            }
        }
        .map { entities -> entities.map { it.toDomainModel() } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun loadItems(tripId: Long) {
        viewModelScope.launch {
            val tripEntity = tripRepository.getTripByIdCached(tripId)
            _trip.value = tripEntity?.toDomainModel()
            tripEntity?.let {
                _selectedDay.value = it.startDateTime.toLocalDate()
            }
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