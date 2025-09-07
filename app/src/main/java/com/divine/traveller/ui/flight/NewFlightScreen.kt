package com.divine.traveller.ui.flight

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.entity.FlightStatus
import com.divine.traveller.data.viewmodel.FlightViewModel
import com.divine.traveller.model.FlightModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFlightScreen(
    modifier: Modifier = Modifier,
    tripId: Long,
    onFlightCreated: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: FlightViewModel = hiltViewModel()
) {
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
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
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
                            val newFlight = FlightModel(
                                id = 0L,
                                tripId = tripId,
                                airline = "Sample Airline",
                                flightNumber = "FL123",
                                departureAirport = "JFK",
                                arrivalAirport = "LAX",
                                departureDate = System.currentTimeMillis(),
                                departureZoneId = "America/New_York",
                                arrivalDate = System.currentTimeMillis() + 5 * 60 * 60 * 1000, // +5 hours
                                arrivalZoneId = "America/Los_Angeles",
                                status = FlightStatus.SCHEDULED
                            )
                            viewModel.insert(newFlight)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text("Add Flight")
//                        Icon(
//                            imageVector = Icons.Default.Add,
//                            contentDescription = "Add new flight",
//                            tint = MaterialTheme.colorScheme.onSurface,
//                            modifier = Modifier.size(24.dp)
//                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Text("Flight Screen for tripId: $tripId", modifier = modifier)
    }
}