package com.divine.traveller.ui.flight

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.statemodel.NewFlightStateModel
import com.divine.traveller.data.viewmodel.FlightViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFlightScreen(
    modifier: Modifier = Modifier,
    tripId: Long,
    onFlightCreated: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    newFlightStateModel: NewFlightStateModel = hiltViewModel(),
    viewModel: FlightViewModel = hiltViewModel()
) {

    val state by newFlightStateModel.uiState.collectAsState()

    val newFlightCreated by viewModel.newFlightCreated.collectAsState()

    LaunchedEffect(newFlightCreated) {
        if (newFlightCreated) {
            onFlightCreated()
            viewModel.newFlightCreated.value = false // reset for next time
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Add Flight",
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
            BottomAppBar(
                actions = {
                    Button(
                        onClick = {
                            Log.d("NewFlightScreen", "Current State: $state")
                            if (state.departureLocalDate != null && state.arrivalLocalDate != null && state.departurePlace != null && state.arrivalPlace != null) {
                                Log.d("NewFlightScreen", "Creating flight with state: $state")

                                viewModel.createNewFlight(tripId, state)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    ) {
                        Text(
                            "Add Flight",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
            )
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
            NewFlightForm(
                state = state,
                onAirlineChange = newFlightStateModel::setAirline,
                onFlightNumberChange = newFlightStateModel::setFlightNumber,
                onDeparturePlaceChange = newFlightStateModel::setDeparturePlace,
                onArrivalPlaceChange = newFlightStateModel::setArrivalPlace,
                onDepartureLocalDateChange = newFlightStateModel::setDepartureLocalDate,
                onArrivalLocalDateChange = newFlightStateModel::setArrivalLocalDate,
                placesClient = viewModel.placesClient,
            )
        }
    }
}

