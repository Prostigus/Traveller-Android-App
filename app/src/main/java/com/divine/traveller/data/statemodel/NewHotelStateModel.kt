package com.divine.traveller.data.statemodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.divine.traveller.data.model.HotelModel
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class NewHotelStateModel @Inject constructor(
    private val state: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val KEY_STATE = "newHotelState"
    }

    private val _state = MutableStateFlow(
        state.get<NewHotelState>(KEY_STATE) ?: NewHotelState()
    )
    val uiState: StateFlow<NewHotelState> = _state.asStateFlow()

    fun setId(v: Long) {
        update { it.copy(id = v) }
    }

    fun setCheckInDate(v: ZonedDateTime) {
        update { it.copy(checkInDate = v) }
    }

    fun setCheckOutDate(v: ZonedDateTime) {
        update { it.copy(checkOutDate = v) }
    }

    fun setBookingReference(v: String) {
        update { it.copy(bookingReference = v) }
    }

    fun setPlace(v: Place) {
        update { it.copy(place = v) }
    }

    fun setCheckInLocalDate(v: LocalDateTime?) {
        update { it.copy(checkInLocalDate = v) }
    }

    fun setCheckOutLocalDate(v: LocalDateTime?) {
        update { it.copy(checkOutLocalDate = v) }
    }

    private fun update(transform: (NewHotelState) -> NewHotelState) {
        val newState = transform(_state.value)
        _state.value = newState
        state[KEY_STATE] = newState
    }

    fun replaceState(hotel: HotelModel, place: Place?) {
        _state.value = NewHotelState(
            id = hotel.id,
            checkInDate = hotel.checkInDate,
            checkOutDate = hotel.checkOutDate,
            bookingReference = hotel.bookingReference ?: "",
            place = place,
            checkInLocalDate = hotel.checkInDate.toLocalDateTime(),
            checkOutLocalDate = hotel.checkOutDate.toLocalDateTime(),
        )
        state[KEY_STATE] = _state.value
    }
}

@Parcelize
data class NewHotelState(
    val id: Long = 0L,
    val checkInDate: ZonedDateTime? = null,
    val checkOutDate: ZonedDateTime? = null,
    val bookingReference: String = "",
    val place: Place? = null,
    val checkInLocalDate: LocalDateTime? = null,
    val checkOutLocalDate: LocalDateTime? = null,
) : Parcelable