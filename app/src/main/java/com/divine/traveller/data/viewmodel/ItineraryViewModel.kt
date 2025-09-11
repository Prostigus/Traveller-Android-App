package com.divine.traveller.data.viewmodel

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.di.DataStoreModule.SELECTED_DAY_KEY
import com.divine.traveller.data.di.DataStoreModule.SELECTED_DAY_TIMESTAMP_KEY
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
import kotlinx.coroutines.flow.first
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
    val placesClient: PlacesClient,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _trip = MutableStateFlow<TripModel?>(null)
    val trip: StateFlow<TripModel?> = _trip.asStateFlow()

    private val _selectedDay = MutableStateFlow<LocalDate?>(null)
    val selectedDay: StateFlow<LocalDate?> = _selectedDay.asStateFlow()

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

    fun selectDay(day: LocalDate) {
        _selectedDay.value = day
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[SELECTED_DAY_KEY] = day.toString()
                prefs[SELECTED_DAY_TIMESTAMP_KEY] = System.currentTimeMillis().toString()
            }
        }
    }

    fun loadItems(tripId: Long) {
        viewModelScope.launch {
            val tripEntity = tripRepository.getTripByIdCached(tripId)
            _trip.value = tripEntity?.toDomainModel()
            tripEntity?.let {
                val tripStartDate = it.startDateTime.toLocalDate()
                val today = LocalDate.now()
                val prefs = dataStore.data.first()
                val savedDay = prefs[SELECTED_DAY_KEY]?.let { LocalDate.parse(it) }
                val savedTimestamp = prefs[SELECTED_DAY_TIMESTAMP_KEY]?.toLongOrNull()
                val now = System.currentTimeMillis()
                val isValid =
                    savedTimestamp != null && (now - savedTimestamp) < 1 * 60 * 60 * 1000 // 24 hours
                val defaultDay = maxOf(tripStartDate, today)
                _selectedDay.value =
                    if (savedDay != null && savedDay >= tripStartDate && isValid) savedDay else defaultDay
            }
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

    fun insertWithNextOrder(
        item: ItineraryItemModel,
        day: LocalDate? = item.dayDate,
        onComplete: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = repository.insertWithNextOrder(item.toEntity(), day)
            onComplete(id)
        }
    }

    fun reorderItemsForDay(tripId: Long, dayDate: LocalDate, orderedIds: List<Long>) {
        viewModelScope.launch {
            Log.d("ItineraryViewModel", "Reordering items for $dayDate: $orderedIds")
            repository.reorderItemsForDay(tripId, dayDate, orderedIds)
        }
    }

}