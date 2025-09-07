package com.divine.traveller.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.model.ItineraryItemModel
import com.divine.traveller.util.millisToLocalDateTimeInZone
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItineraryItemBottomSheet(
    visible: Boolean,
    selectedDateTime: LocalDateTime?,
    timeZone: ZoneId,
    tripId: Long,
    viewModel: ItineraryViewModel,
    selectedItem: ItineraryItemModel? = null,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var placeId by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ItineraryCategory.OTHER) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val startTimePickerState = rememberTimePickerState(
        initialHour = selectedDateTime?.hour ?: 9,
        initialMinute = selectedDateTime?.minute ?: 0
    )
    val endTimePickerState = rememberTimePickerState(
        initialHour = selectedDateTime?.plusHours(1)?.hour ?: 10,
        initialMinute = selectedDateTime?.minute ?: 0
    )

    LaunchedEffect(selectedItem) {
        title = selectedItem?.title ?: ""
        description = selectedItem?.description ?: ""
        destination = selectedItem?.placeId ?: ""
        placeId = selectedItem?.placeId ?: ""
        selectedCategory = selectedItem?.category ?: ItineraryCategory.OTHER
        if (selectedItem != null) {
            val startLocal = millisToLocalDateTimeInZone(selectedItem.startDateTime, timeZone)
            val endLocal = millisToLocalDateTimeInZone(selectedItem.endDateTime, timeZone)
            startTimePickerState.hour = startLocal.hour
            startTimePickerState.minute = startLocal.minute
            endTimePickerState.hour = endLocal.hour
            endTimePickerState.minute = endLocal.minute
        } else if (selectedDateTime != null) {
            startTimePickerState.hour = selectedDateTime.hour
            startTimePickerState.minute = selectedDateTime.minute
            endTimePickerState.hour = selectedDateTime.plusHours(1).hour
            endTimePickerState.minute = selectedDateTime.minute
        }
    }

    if (visible && selectedDateTime != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add Itinerary Item",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                PlacesAutocompleteTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    onPlaceSelected = { selectedPlace ->
                        destination = selectedPlace.displayName ?: ""
                        placeId = selectedPlace.id ?: ""
                    },
                    placesClient = viewModel.placesClient,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Destination",
                    placeholder = "Search for a place...",
                    includedType = "locality"
                )

                // Category Selection
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.name.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        leadingIcon = {
                            Icon(Icons.Default.Star, contentDescription = null)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        ItineraryCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        category.name.lowercase()
                                            .replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Time Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showStartTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Start: ${
                                String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    startTimePickerState.hour,
                                    startTimePickerState.minute
                                )
                            }"
                        )
                    }

                    FilledTonalButton(
                        onClick = { showEndTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "End: ${
                                String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    endTimePickerState.hour,
                                    endTimePickerState.minute
                                )
                            }"
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val startDateTime = selectedDateTime
                            .withHour(startTimePickerState.hour)
                            .withMinute(startTimePickerState.minute)
                        val endDateTime = selectedDateTime
                            .withHour(endTimePickerState.hour)
                            .withMinute(endTimePickerState.minute)

                        val startInstant = startDateTime.atZone(timeZone).toInstant()
                        val endInstant = endDateTime.atZone(timeZone).toInstant()

                        val newItem = ItineraryItemModel(
                            id = selectedItem?.id ?: 0,
                            tripId = tripId,
                            title = title,
                            description = description,
                            placeId = placeId,
                            viewType = "view",
                            startDateTime = startInstant.toEpochMilli(),
                            endDateTime = endInstant.toEpochMilli(),
                            category = selectedCategory,
                            status = ItineraryItemStatus.PENDING
                        )
                        viewModel.insert(newItem)

                        // Reset form
                        title = ""
                        description = ""
                        destination = ""
                        placeId = ""
                        selectedCategory = ItineraryCategory.OTHER
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = title.isNotBlank()
                ) {
                    Text("Save Itinerary Item")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Time Picker Dialogs
    if (showStartTimePicker) {
        TimePickerDialog(
            timePickerState = startTimePickerState,
            onDismiss = { showStartTimePicker = false },
            onConfirm = { showStartTimePicker = false }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            timePickerState = endTimePickerState,
            onDismiss = { showEndTimePicker = false },
            onConfirm = { showEndTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    timePickerState: TimePickerState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}