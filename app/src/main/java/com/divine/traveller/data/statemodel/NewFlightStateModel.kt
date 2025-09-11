package com.divine.traveller.data.statemodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.divine.traveller.data.entity.FlightStatus
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import net.iakovlev.timeshape.TimeZoneEngine
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class NewFlightStateModel @Inject constructor(
    private val state: SavedStateHandle,
    private val timeZoneEngineDeferred: Deferred<TimeZoneEngine>
) : ViewModel() {

    companion object {
        private const val KEY_STATE = "newFlightState"
    }

    private val _state = MutableStateFlow(state.get<NewFlightState>(KEY_STATE) ?: NewFlightState())
    val uiState: StateFlow<NewFlightState> = _state.asStateFlow()

    fun setAirline(v: String) = update { it.copy(airline = v) }
    fun setFlightNumber(v: String) = update { it.copy(flightNumber = v) }
    fun setDeparturePlace(v: Place) {
        update { it.copy(departurePlace = v) }
        maybeUpdateDepartureDateTime()
    }

    fun setArrivalPlace(v: Place) {
        update { it.copy(arrivalPlace = v) }
        maybeUpdateArrivalDateTime()
    }
    fun setDepartureDate(v: ZonedDateTime?) = update { it.copy(departureDateTime = v) }
    fun setArrivalDate(v: ZonedDateTime?) = update { it.copy(arrivalDateTime = v) }
    fun setStatus(v: FlightStatus) = update { it.copy(status = v) }

    fun setDepartureLocalDate(v: LocalDateTime?) {
        update { it.copy(departureLocalDate = v) }
        maybeUpdateDepartureDateTime()
    }

    fun setArrivalLocalDate(v: LocalDateTime?) {
        update { it.copy(arrivalLocalDate = v) }
        maybeUpdateArrivalDateTime()
    }

    private fun update(transform: (NewFlightState) -> NewFlightState) {
        val newState = transform(_state.value)
        _state.value = newState
        state[KEY_STATE] = newState
    }

    private fun maybeUpdateDepartureDateTime() {
        val currentState = _state.value
        val place = currentState.departurePlace
        val localDate = currentState.departureLocalDate
        if (place != null && localDate != null) {
            viewModelScope.launch {
                val engine = timeZoneEngineDeferred.await()
                val latLng = place.location // adapt as needed
                val zoneId = engine.query(latLng.latitude, latLng.longitude)
                    ?.orElse(ZoneId.of("UTC"))
                val zonedDateTime = localDate.atZone(zoneId)
                setDepartureDate(zonedDateTime)
            }
        }
    }

    private fun maybeUpdateArrivalDateTime() {
        val currentState = _state.value
        val place = currentState.arrivalPlace
        val localDate = currentState.arrivalLocalDate
        if (place != null && localDate != null) {
            viewModelScope.launch {
                val engine = timeZoneEngineDeferred.await()
                val latLng = place.location // adapt as needed
                val zoneId = engine.query(latLng.latitude, latLng.longitude)
                    ?.orElse(ZoneId.of("UTC"))
                val zonedDateTime = localDate.atZone(zoneId)
                setArrivalDate(zonedDateTime)
            }
        }
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
    val status: FlightStatus = FlightStatus.SCHEDULED,
    val departureLocalDate: LocalDateTime? = null,
    val arrivalLocalDate: LocalDateTime? = null
) : Parcelable