package com.divine.traveller.ui.itinerary

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.model.tripDatesAsLocalDates
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
    val selectedDay = viewModel.selectedDay.collectAsState(null)
    val timelineScrollState = rememberLazyListState()

    var showSheet by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedItem by remember { mutableStateOf<ItineraryItemModel?>(null) }

    val density = LocalDensity.current
    val expandedCalendarDp = 420.dp
    val collapsedCalendarDp = 160.dp // small but accessible header
    val expandedPx = with(density) { expandedCalendarDp.toPx() }
    val collapsedPx = with(density) { collapsedCalendarDp.toPx() }
    val maxOffset = (expandedPx - collapsedPx).coerceAtLeast(0f)
    var calendarOffset by remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // available.y is negative when scrolling up (content moves up)
                if (available.y < 0f && calendarOffset < maxOffset) {
                    val delta = -available.y
                    val newOffset = (calendarOffset + delta).coerceAtMost(maxOffset)
                    val consumed = newOffset - calendarOffset
                    calendarOffset = newOffset
                    // consumed is positive -> we consumed upward scroll, so return negative y
                    return Offset(0f, -consumed)
                } else if (available.y > 0f && calendarOffset > 0f) {
                    // scrolling down: expand calendar if there's collapsed offset
                    val delta = available.y
                    val newOffset = (calendarOffset - delta).coerceAtLeast(0f)
                    val consumed = calendarOffset - newOffset
                    calendarOffset = newOffset
                    // consumed is positive -> we consumed downward scroll, so return positive y
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }
        }
    }

    val calendarHeight by animateDpAsState(
        targetValue = with(density) {
            (expandedPx - calendarOffset).coerceIn(collapsedPx, expandedPx).toDp()
        },
        animationSpec = tween(durationMillis = 200),
        label = "calendar_height"
    )

    val isCalendarExpandedComputed by remember {
        derivedStateOf { calendarOffset < (maxOffset * 0.95f) } // mostly expanded if little offset
    }

    LaunchedEffect(tripId) {
        Log.d("TripItineraryScreen", "Rendering itinerary for tripId: $tripId")
        viewModel.loadItems(tripId)
    }

    val itemsForDay by viewModel.itemsForDay.collectAsState()
    val trip by viewModel.trip.collectAsState()
    val timeZone = trip?.let { toZoneId(it.destinationZoneIdString) } ?: ZoneId.systemDefault()

    Scaffold(
        modifier = modifier,
        topBar = { /* ... */ },
        bottomBar = {
            ItineraryNavBar(
                selectedScreen = TRIP_DETAILS,
                onNavigate = onNavigate,
                tripId = tripId
            )
        },
        floatingActionButton = {
            IconButton(
                onClick = {
                    showSheet = true
                    selectedDate = selectedDay.value
                    selectedItem = null
                },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new itinerary item",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(nestedScrollConnection)
        ) {
            // Calendar - shown at top, will be compressed when scrolling timeline

            ItineraryCalendar(
                itemsPerDay = itemsForDay,
                selectedDay = selectedDay.value,
                tripDates = trip?.tripDatesAsLocalDates ?: emptySet(),
                isExpanded = isCalendarExpandedComputed,
                onClickDay = { day ->
                    viewModel.selectDay(day)
                },
                onToggleExpanded = {
                    calendarOffset = if (calendarOffset > maxOffset / 2f) {
                        0f // expand
                    } else {
                        maxOffset // collapse
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calendarHeight)
            )

            selectedDay.let { day ->
                if (itemsForDay.isNotEmpty()) {
                    ItineraryDayTimeline(
                        day = day.value ?: LocalDate.now(),
                        itemsForDay = itemsForDay,
                        viewModel = viewModel,
                        tripId = tripId,
                        lazyListState = timelineScrollState,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }

            NewItineraryItemBottomSheet(
                visible = showSheet,
                selectedDate = selectedDate,
                tripId = tripId,
                viewModel = viewModel,
                selectedItem = selectedItem,
                onDismiss = {
                    showSheet = false
                    selectedDate = null
                    selectedItem = null
                },
            )
        }
    }
}