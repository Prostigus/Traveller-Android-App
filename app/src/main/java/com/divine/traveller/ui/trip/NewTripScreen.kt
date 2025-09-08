package com.divine.traveller.ui.trip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.R
import com.divine.traveller.data.model.TripModel
import com.divine.traveller.data.statemodel.NewTripStateModel
import com.divine.traveller.data.viewmodel.HomeViewModel
import com.divine.traveller.ui.composable.FormFieldWithIcon
import com.divine.traveller.ui.composable.PlacesAutocompleteTextField
import com.divine.traveller.util.correctUtcTimeStampForZonedDate
import com.google.android.libraries.places.api.model.PlaceTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.iakovlev.timeshape.TimeZoneEngine
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    newTripStateModel: NewTripStateModel = hiltViewModel(),
    onTripCreated: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val state by newTripStateModel.uiState.collectAsState()
    val tripName = state.tripName
    val destination = state.destination
    val description = state.description
    val startDate = state.startDate
    val endDate = state.endDate
    val destinationZoneIdString = state.destinationZoneIdString

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).apply({
            timeZone = TimeZone.getTimeZone("UTC")
        })
    }
    var timeZoneEngine by remember { mutableStateOf<TimeZoneEngine?>(null) }

    LaunchedEffect(Unit) {
        timeZoneEngine = withContext(Dispatchers.Default) {
            viewModel.getTimeZoneEngine()
        }
    }


    // Get current date at start of day to prevent past date selection
    val today = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate ?: today,
    )

    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = endDate ?: today
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "New Trip",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        // Empty space to center title
                        Spacer(modifier = Modifier.width(48.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (tripName.isNotBlank() && destination.isNotBlank() && startDate != null && endDate != null) {
                                val tripModel = TripModel(
                                    name = tripName,
                                    destination = destination,
                                    description = description.takeIf { it.isNotBlank() },
                                    budget = null,
                                    startDateUtcMillis = correctUtcTimeStampForZonedDate(
                                        startDate,
                                        ZoneId.of(destinationZoneIdString)
                                    ),
                                    endDateUtcMillis = correctUtcTimeStampForZonedDate(
                                        endDate,
                                        ZoneId.of(destinationZoneIdString),
                                        true
                                    ),
                                    destinationZoneIdString = destinationZoneIdString
                                )
                                viewModel.addTrip(tripModel)
                                onTripCreated()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = tripName.isNotBlank() && destination.isNotBlank() && startDate != null && endDate != null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Create Trip",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Trip Name Field
            FormFieldWithIcon(
                value = tripName,
                onValueChange = { newTripStateModel.setTripName(it) },
                label = "Trip Name",
                placeholder = "e.g. Alpine Adventure",
                icon = Icons.Default.Favorite
            )

            // Destination Field
            Column {
                Text(
                    text = "Destination",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )


                PlacesAutocompleteTextField(
                    onPlaceSelected = { selectedPlace ->
                        newTripStateModel.setDestination(selectedPlace.displayName ?: "")
                        newTripStateModel.setDestinationZoneIdString(selectedPlace.location?.let { latLng ->
                            val id = timeZoneEngine?.query(latLng.latitude, latLng.longitude)
                                ?.orElse(ZoneId.of("UTC"))?.id
                            id
                        } ?: "UTC")
                    },
                    placesClient = viewModel.placesClient,
                    modifier = Modifier.fillMaxWidth(),
                    label = "City, Country",
                    placeholder = "Enter a destination...",
                    includedTypes = listOf(PlaceTypes.REGIONS),
                )
            }

            // Date Fields Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Start Date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Start Date",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DatePickerField(
                        selectedDate = startDate,
                        dateFormatter = dateFormatter,
                        onClick = { showStartDatePicker = true }
                    )
                }

                // End Date
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "End Date",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DatePickerField(
                        selectedDate = endDate,
                        dateFormatter = dateFormatter,
                        onClick = { showEndDatePicker = true }
                    )
                }
            }

            // Description Field
            Column {
                Row {
                    Text(
                        text = "Description ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "(Optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { newTripStateModel.setDescription(it) },
                    placeholder = {
                        Text(
                            "Add a description for your trip...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Cover Photo Field
            Column {
                Text(
                    text = "Cover Photo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { /* TODO: Handle photo upload */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_cloud_upload),
                            contentDescription = "Upload photo",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row {
                                Text(
                                    text = "Click to upload",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = " or drag and drop",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "SVG, PNG, JPG (MAX. 800x400px)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Add bottom spacing
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Start Date Picker Dialog
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            newTripStateModel.setStartDate(millis)
                        }

                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    // End Date Picker Dialog
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
//                            endDate = correctUtcTimeStampForLocalDate(millis, ZoneId.systemDefault(), true)
                            newTripStateModel.setEndDate(millis)
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}

@Composable
private fun DatePickerField(
    selectedDate: Long?,
    dateFormatter: SimpleDateFormat,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = selectedDate?.let { dateFormatter.format(it) } ?: "",
        onValueChange = { },
        label = {
            Text(
                text = "Date",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        placeholder = {
            Text(
                "Select date",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select date",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        enabled = false,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledTrailingIconColor = MaterialTheme.colorScheme.primary
        )
    )
}