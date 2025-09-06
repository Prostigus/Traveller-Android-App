package com.divine.traveller.ui.trips

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.model.ItineraryItemModel
import com.divine.traveller.ui.composable.AddItineraryItemBottomSheet
import com.divine.traveller.ui.composable.ItineraryCalendar
import com.divine.traveller.ui.composable.ItineraryDayTimeLine
import com.divine.traveller.util.toZoneId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripItineraryScreen(
    modifier: Modifier = Modifier,
    tripId: Long,
    viewModel: ItineraryViewModel = hiltViewModel(),
    onTripCreated: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onInsertItem: (ItineraryItemModel) -> Unit = {}
) {
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(tripId) {
        Log.d("TripItineraryScreen", "Rendering itinerary for tripId: $tripId")
        viewModel.loadItems(tripId)
    }

    val itemsByDay by viewModel.itemsByDay.collectAsState()
    val trip by viewModel.trip.collectAsState()
    val timeZone = trip?.let { toZoneId(it.destinationZoneIdString) } ?: ZoneId.systemDefault()

    Scaffold(
        modifier = modifier,
        topBar = { /* ... */ },
        bottomBar = { TripScreenNavBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Calendar - shown at top, will be compressed when scrolling timeline

                ItineraryCalendar(
                    itemsPerDay = itemsByDay,
                    selectedDay = selectedDay,
                    onClickDay = { day ->
                        selectedDay = day
                    },
                    modifier = Modifier.fillMaxWidth()
                )

            selectedDay?.let { day ->
                val itemsForDay = itemsByDay.find { it.first == day }?.second ?: emptyList()
                if(itemsByDay.any { it.first == day }){
                    ItineraryDayTimeLine(
                        itemsForDay = itemsForDay,
                        timeZone = timeZone,
                        viewModel = viewModel,
                        tripId = tripId,
//                        onAddItem = { hour, minute ->
//                            selectedDateTime = day.atTime(hour, minute)
//                            showSheet = true
//                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}