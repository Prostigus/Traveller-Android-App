package com.divine.traveller.data.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class NewTripViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_STATE = "newTripState"
    }

    private val _state = MutableStateFlow(state.get<NewTripState>(KEY_STATE) ?: NewTripState())
    val uiState: StateFlow<NewTripState> = _state.asStateFlow()

    fun setTripName(v: String) = update { it.copy(tripName = v) }
    fun setDestination(v: String) = update { it.copy(destination = v) }
    fun setDescription(v: String) = update { it.copy(description = v) }
    fun setStartDate(v: Long?) = update { it.copy(startDate = v) }
    fun setEndDate(v: Long?) = update { it.copy(endDate = v) }
    fun setDestinationZoneIdString(v: String) = update { it.copy(destinationZoneIdString = v) }

    private fun update(transform: (NewTripState) -> NewTripState) {
        viewModelScope.launch {
            val newState = transform(_state.value)
            _state.value = newState
            state[KEY_STATE] = newState
        }
    }
}

@Parcelize
data class NewTripState(
    val tripName: String = "",
    val destination: String = "",
    val description: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val destinationZoneIdString: String = "UTC"
) : Parcelable