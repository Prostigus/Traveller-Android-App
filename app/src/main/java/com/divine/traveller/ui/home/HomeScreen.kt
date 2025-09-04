package com.divine.traveller.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.viewmodel.TripViewModel
import com.divine.traveller.model.Trip
import com.divine.traveller.ui.composable.ActionIcon
import com.divine.traveller.ui.trips.TripCard
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: TripViewModel = hiltViewModel(),
    onNavigateToNewTrip: () -> Unit = {}
) {
    val trips by viewModel.allTrips.collectAsState()
    val today = Date()
    val currentTrips = trips.filter { it.startDate <= today && it.endDate >= today }
    val otherTrips = trips.filterNot { it.startDate <= today && it.endDate >= today }


    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(
                    "Trips",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ) }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionIcon(imageVector = Icons.Default.Add, contentDescription = "Add new trip", onClick = onNavigateToNewTrip)
                        ActionIcon(imageVector = Icons.Default.AccountCircle, contentDescription = "User", onClick = {})
                    }
                }
            )
        }
    ) { paddingValues ->
        Text("Current Trips", modifier = Modifier.padding(start = 16.dp, top = 8.dp), fontWeight = FontWeight.SemiBold)
        Column(modifier = Modifier.padding(paddingValues)) {
            if (currentTrips.isNotEmpty()) {
                Column {
                    currentTrips.forEach { trip ->
                        TripCard(
                            trip = trip,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            onComplete = { viewModel.markTripCompleted(trip.id) },
                            onDelete = { viewModel.deleteTrip(trip) }
                        )
                    }
                }
            }
            if (otherTrips.isEmpty() && currentTrips.isEmpty()) {
                EmptyTripsState(onNavigateToNewTrip = onNavigateToNewTrip)
            } else {
                TripsList(
                    trips = otherTrips,
                    onTripComplete = viewModel::markTripCompleted,
                    onTripDelete = viewModel::deleteTrip
                )
            }
        }
    }
}

@Composable
fun TripsList(
    trips: List<Trip>,
    modifier: Modifier = Modifier,
    onTripComplete: (Long) -> Unit,
    onTripDelete: (Trip) -> Unit
) {
    LazyRow(modifier = modifier.fillMaxSize()) {
        items(trips) { trip ->
            TripCard(
                trip = trip,
                onComplete = { onTripComplete(trip.id) },
                onDelete = { onTripDelete(trip) },
                modifier = Modifier
                    .width(256.dp)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun EmptyTripsState(
    modifier: Modifier = Modifier,
    onNavigateToNewTrip: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No trips yet",
                modifier = Modifier.padding(bottom = 16.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Tap the + button to create your first trip",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}