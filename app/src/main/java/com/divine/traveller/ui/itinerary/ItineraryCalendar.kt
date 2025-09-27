package com.divine.traveller.ui.itinerary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ItineraryCalendar(
    modifier: Modifier = Modifier,
    viewModel: ItineraryViewModel,
    itemsPerDay: List<ItineraryItemModel>,
    selectedDay: LocalDate? = null,
    tripDates: Set<LocalDate> = emptySet(),
    isExpanded: Boolean = true,
    onClickDay: (LocalDate) -> Unit = {},
    onToggleExpanded: () -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val hotelBookingsForTripByDay by viewModel.hotelBookingsForTripByDay.collectAsState(emptyMap())


    // Auto-select today if it's in the trip dates and no day is selected
//    LaunchedEffect(itemsPerDay, selectedDay) {
//        if (selectedDay == null && tripDates.contains(LocalDate.now())) {
//            onClickDay(LocalDate.now())
//        }
//    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onToggleExpanded() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Month header with navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isExpanded) 16.dp else 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "${
                        currentMonth.month.getDisplayName(
                            TextStyle.FULL,
                            Locale.getDefault()
                        )
                    } ${currentMonth.year}",
                    fontSize = if (isExpanded) 20.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (isExpanded) {
                // Full calendar grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Day headers
                    items(listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")) { dayName ->
                        Text(
                            text = dayName,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    val monthDays = viewModel.getMonthDays(currentMonth)
                    items(monthDays) { date ->
                        val items = itemsPerDay
                        val isCurrentMonth = date?.month == currentMonth.month
                        val isTripDay = date?.let { tripDates.contains(it) } ?: false

                        if (date != null) {
                            CalendarDayItem(
                                date = date,
                                items = items,
                                isSelected = date == selectedDay,
                                isCurrentMonth = isCurrentMonth,
                                isTripDay = isTripDay,
                                isToday = date == LocalDate.now(),
                                onClick = { onClickDay(date) },
                                compact = false
                            )
                        }
                    }
                }
            } else {
                val monthDays = viewModel.getMonthDays(currentMonth)
                val tripDaysInMonth = monthDays
                    .filterNotNull()
                    .filter { it in tripDates }
                    .sorted()

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tripDaysInMonth) { date ->
                        CalendarDayItem(
                            date = date,
                            items = itemsPerDay,
                            isSelected = date == selectedDay,
                            isCurrentMonth = date.month == currentMonth.month,
                            isTripDay = tripDates.contains(date),
                            isToday = date == LocalDate.now(),
                            onClick = { onClickDay(date) },
                            compact = true
                        )
                    }
                }
            }
        }
    }
}