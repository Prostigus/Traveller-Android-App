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
import com.divine.traveller.data.model.HotelModel
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.model.TripModel
import com.divine.traveller.data.repository.HotelRepository
import com.divine.traveller.data.repository.ItineraryItemRepository
import com.divine.traveller.data.repository.PlaceRepository
import com.divine.traveller.data.repository.TripRepository
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
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ItineraryViewModel @Inject constructor(
    private val repository: ItineraryItemRepository,
    private val tripRepository: TripRepository,
    val placeRepository: PlaceRepository,
    private val dataStore: DataStore<Preferences>,
    private val hotelRepository: HotelRepository
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val hotelBookingsByDay: StateFlow<Map<LocalDate, HotelsByDay>> = _trip
        .filterNotNull()
        .flatMapLatest { trip ->
            hotelRepository.getByTripId(trip.id).map { entities ->
                val hotels = entities.map { it.toDomainModel() }.sortedBy { it.checkInDate }

                val tripStartDay = trip.startDateTime.toLocalDate()
                val tripLastDay = trip.endDateTime.toLocalDate()

                // Initialize map with every trip day as an empty list
                val map = getAccommodationDays(tripStartDay, tripLastDay)
                    .associateWith { mutableListOf<HotelModel>() }
                    .toMutableMap()

                // Add each hotel to the days it spans (clipped to the trip range)
                hotels.forEach { hotel ->
                    val hotelStart = hotel.checkInDate.toLocalDate().coerceAtLeast(tripStartDay)
                    val hotelEndExclusive = hotel.checkOutDate.toLocalDate().plusDays(1)
                        .coerceAtMost(tripLastDay.plusDays(1))

                    var d = hotelStart
                    while (d.isBefore(hotelEndExclusive)) {
                        map[d]?.add(hotel) // safe because map has all trip days
                        d = d.plusDays(1)
                    }
                }

                // Convert to immutable HotelsByDay
                map.mapValues { (day, list) ->
                    HotelsByDay(
                        isNightBooked = checkDateHasHotelNightBooking(day, list, tripLastDay),
                        hotels = list.toList()
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    fun checkDateHasHotelNightBooking(
        date: LocalDate,
        hotelList: List<HotelModel>,
        tripLastDay: LocalDate
    ): Boolean {
        if (hotelList.isEmpty()) return false
        if (hotelList.size == 1 && hotelList[0].checkOutDate.toLocalDate() == date && date != tripLastDay) return false
        if (hotelList.size > 1 && hotelList.any { it.checkOutDate.toLocalDate() == date } && !hotelList.any { it.checkInDate.toLocalDate() == date } && date != tripLastDay) return false
        return true
    }

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

    private fun getAccommodationDays(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val days = mutableListOf<LocalDate>()
        var currentDay = startDate
        while (!currentDay.isAfter(endDate)) {
            days.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }
        return days
    }

    fun getMonthDays(yearMonth: YearMonth): List<LocalDate?> {
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

        val days = mutableListOf<LocalDate?>()

        // Add empty cells for days before the first day of the month
        repeat(firstDayOfWeek) {
            days.add(null)
        }

        // Add all days of the month
        var currentDay = firstDayOfMonth
        while (!currentDay.isAfter(lastDayOfMonth)) {
            days.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }

        // Fill remaining cells to complete the grid (6 rows * 7 days = 42 cells)
        while (days.size < 42) {
            days.add(null)
        }

        return days
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

data class HotelsByDay(
    val isNightBooked: Boolean,
    val hotels: List<HotelModel>
)