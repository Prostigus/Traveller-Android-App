package com.divine.traveller.ui.itinerary

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.navigation.Routes.TRIP_DETAILS
import com.divine.traveller.ui.composable.ItineraryNavBar
import com.divine.traveller.util.toZoneId
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    modifier: Modifier = Modifier,
    tripId: Long,
    viewModel: ItineraryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var isCalendarExpanded by remember { mutableStateOf(true) }
    val timelineScrollState = rememberLazyListState()

    // Track if user has scrolled down
    val hasScrolledDown by remember {
        derivedStateOf {
            timelineScrollState.firstVisibleItemIndex > 0 ||
                    timelineScrollState.firstVisibleItemScrollOffset > 50
        }
    }

    LaunchedEffect(hasScrolledDown) {
        if (hasScrolledDown && isCalendarExpanded) {
            isCalendarExpanded = false
        } else if (!hasScrolledDown && !isCalendarExpanded) {
            isCalendarExpanded = true
        }
    }

    LaunchedEffect(tripId) {
        Log.d("TripItineraryScreen", "Rendering itinerary for tripId: $tripId")
        viewModel.loadItems(tripId)
    }

    val itemsByDay by viewModel.itemsByDay.collectAsState()
    val trip by viewModel.trip.collectAsState()
    val timeZone = trip?.let { toZoneId(it.destinationZoneIdString) } ?: ZoneId.systemDefault()

    // Animate calendar height - shrink to 150dp instead of 80dp
    val calendarHeight by animateDpAsState(
        targetValue = if (isCalendarExpanded) 420.dp else 300.dp,
        animationSpec = tween(durationMillis = 300),
        label = "calendar_height"
    )

    Scaffold(
        modifier = modifier,
        topBar = { /* ... */ },
        bottomBar = {
            ItineraryNavBar(
                selectedScreen = TRIP_DETAILS,
                onNavigate = onNavigate,
                tripId = tripId
            )
        }
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
                isExpanded = isCalendarExpanded,
                onClickDay = { day ->
                    selectedDay = day
                },
                onToggleExpanded = {
                    isCalendarExpanded = !isCalendarExpanded
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calendarHeight)
            )

            selectedDay?.let { day ->
                val itemsForDay = itemsByDay.find { it.first == day }?.second ?: emptyList()
                if (itemsByDay.any { it.first == day }) {
                    ItineraryDayTimeline(
                        day = day.atStartOfDay(),
                        itemsForDay = itemsForDay,
                        timeZone = timeZone,
                        viewModel = viewModel,
                        tripId = tripId,
//                        onAddItem = { hour, minute ->
//                            selectedDateTime = day.atTime(hour, minute)
//                            showSheet = true
//                        },
                        lazyListState = timelineScrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}