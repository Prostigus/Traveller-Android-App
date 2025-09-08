package com.divine.traveller.data.statemodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.divine.traveller.data.entity.FlightStatus
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class NewFlightStateModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {
    companion object {
        private const val KEY_STATE = "newFlightState"
    }

    private val _state = MutableStateFlow(state.get<NewFlightState>(KEY_STATE) ?: NewFlightState())
    val uiState: StateFlow<NewFlightState> = _state.asStateFlow()

    fun setAirline(v: String) = update { it.copy(airline = v) }
    fun setFlightNumber(v: String) = update { it.copy(flightNumber = v) }
    fun setDeparturePlace(v: Place) = update { it.copy(departurePlace = v) }
    fun setArrivalPlace(v: Place) = update { it.copy(arrivalPlace = v) }
    fun setDepartureDate(v: ZonedDateTime?) = update { it.copy(departureDateTime = v) }
    fun setArrivalDate(v: ZonedDateTime?) = update { it.copy(arrivalDateTime = v) }
    fun setStatus(v: FlightStatus) = update { it.copy(status = v) }

    private fun update(transform: (NewFlightState) -> NewFlightState) {
        val newState = transform(_state.value)
        _state.value = newState
        state[KEY_STATE] = newState
    }
}

@Parcelize
data class NewFlightState(
    val airline: String = "",
    val flightNumber: String = "",
    val departurePlace: Place? = null,
    val arrivalPlace: Place? = null,
    val departureDateTime: ZonedDateTime? = null,
    val arrivalDateTime: ZonedDateTime? = null,
    val status: FlightStatus = FlightStatus.SCHEDULED
) : Parcelable