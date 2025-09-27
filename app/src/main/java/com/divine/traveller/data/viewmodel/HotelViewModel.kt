package com.divine.traveller.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.entity.HotelStatus
import com.divine.traveller.data.mapper.toDomainModel
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.model.HotelModel
import com.divine.traveller.data.repository.HotelRepository
import com.divine.traveller.data.repository.PlaceRepository
import com.divine.traveller.data.statemodel.NewHotelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
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
import net.iakovlev.timeshape.TimeZoneEngine
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val repository: HotelRepository,
    val placeRepository: PlaceRepository,
    private val timeZoneEngineDeferred: Deferred<TimeZoneEngine>
) : ViewModel() {

    var tripId = MutableStateFlow<Long?>(null)
    private val _newHotelCreation =
        MutableStateFlow<NewHotelCreationState>(NewHotelCreationState.Idle)
    val newHotelCreation: StateFlow<NewHotelCreationState> = _newHotelCreation.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val hotelItems: StateFlow<List<HotelModel>> = tripId
        .filterNotNull()
        .flatMapLatest { id -> repository.getByTripId(id) }
        .map { list -> list.map { it.toDomainModel() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createNewHotel(tripId: Long, state: NewHotelState) {
        viewModelScope.launch {
            try {
                _newHotelCreation.value = NewHotelCreationState.Creating

                val engine = timeZoneEngineDeferred.await()
                val latLng = state.place?.location
                val zoneId = latLng
                    ?.let { engine.query(it.latitude, it.longitude)?.orElse(null) }
                    ?: ZoneId.systemDefault()

                // Prefer LocalDateTime (preserves time) if present; fall back to LocalDate at start of day
                val checkInZdt = state.checkInLocalDate?.atZone(zoneId)
                val checkOutZdt = state.checkOutLocalDate?.atZone(zoneId)

                if (checkInZdt == null || checkOutZdt == null) {
                    _newHotelCreation.value =
                        NewHotelCreationState.Error("Check-in and check-out date/time required")
                    return@launch
                }

                if (checkOutZdt.isBefore(checkInZdt)) {
                    _newHotelCreation.value =
                        NewHotelCreationState.Error("Check-out date/time cannot be before check-in date/time")
                    return@launch
                }

                Log.d("HotelViewModel", "Creating new hotel with state: $state")
                val hotelEntity = repository.upsertHotelWithItinerary(
                    HotelModel(
                        id = state.id,
                        tripId = tripId,
                        name = state.place?.displayName ?: "Hotel",
                        address = state.place?.formattedAddress,
                        checkInDate = checkInZdt,
                        checkOutDate = checkOutZdt,
                        bookingReference = state.bookingReference,
                        placeId = state.place?.id,
                        status = HotelStatus.BOOKED
                    ).toEntity()
                )
                Log.d("HotelViewModel", "Created new hotel with ID: $hotelEntity")
                _newHotelCreation.value = NewHotelCreationState.Success

            } catch (e: Exception) {
                Log.d("HotelViewModel", "Error creating new hotel: ${e.message}")
                _newHotelCreation.value = NewHotelCreationState.Error(e.message)

            }
        }
    }

    fun resetNewHotelCreation() {
        _newHotelCreation.value = NewHotelCreationState.Idle
    }

    suspend fun getById(id: Long): HotelModel? {
        return repository.getById(id)?.toDomainModel()
    }

    fun delete(hotel: HotelModel, onComplete: () -> Unit = {}) = viewModelScope.launch {
        repository.delete(hotel.toEntity())
        onComplete()
    }
}

sealed class NewHotelCreationState {
    data object Idle : NewHotelCreationState()
    data object Creating : NewHotelCreationState()
    data object Success : NewHotelCreationState()
    data class Error(val message: String? = null) : NewHotelCreationState()
}