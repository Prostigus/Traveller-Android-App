package com.divine.traveller.data.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewTripViewModel @Inject constructor(private val state: SavedStateHandle) : ViewModel() {

    companion object {
        private const val KEY_TRIP_NAME = "tripName"
        private const val KEY_DESTINATION = "destination"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_START_DATE = "startDate"
        private const val KEY_END_DATE = "endDate"
        private const val KEY_ZONE = "destinationZoneIdString"
    }

    private val _tripName = MutableStateFlow(state.get<String>(KEY_TRIP_NAME) ?: "")
    val tripName: StateFlow<String> = _tripName.asStateFlow()

    private val _destination = MutableStateFlow(state.get<String>(KEY_DESTINATION) ?: "")
    val destination: StateFlow<String> = _destination.asStateFlow()

    private val _description = MutableStateFlow(state.get<String>(KEY_DESCRIPTION) ?: "")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _startDate = MutableStateFlow(state.get<Long?>(KEY_START_DATE))
    val startDate: StateFlow<Long?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow(state.get<Long?>(KEY_END_DATE))
    val endDate: StateFlow<Long?> = _endDate.asStateFlow()

    private val _zone = MutableStateFlow(state.get<String>(KEY_ZONE) ?: "UTC")
    val destinationZoneIdString: StateFlow<String> = _zone.asStateFlow()

    fun setTripName(v: String) = updateState(KEY_TRIP_NAME) { _tripName.value = v }
    fun setDestination(v: String) = updateState(KEY_DESTINATION) { _destination.value = v }
    fun setDescription(v: String) = updateState(KEY_DESCRIPTION) { _description.value = v }
    fun setStartDate(v: Long?) = updateState(KEY_START_DATE) { _startDate.value = v }
    fun setEndDate(v: Long?) = updateState(KEY_END_DATE) { _endDate.value = v }
    fun setDestinationZoneIdString(v: String) = updateState(KEY_ZONE) { _zone.value = v }

    private fun updateState(key: String, block: () -> Unit) {
        viewModelScope.launch {
            block()
            // write latest to SavedStateHandle so it survives process death
            when (key) {
                KEY_TRIP_NAME -> state[KEY_TRIP_NAME] = _tripName.value
                KEY_DESTINATION -> state[KEY_DESTINATION] = _destination.value
                KEY_DESCRIPTION -> state[KEY_DESCRIPTION] = _description.value
                KEY_START_DATE -> state[KEY_START_DATE] = _startDate.value
                KEY_END_DATE -> state[KEY_END_DATE] = _endDate.value
                KEY_ZONE -> state[KEY_ZONE] = _zone.value
            }
        }
    }
}