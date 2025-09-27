package com.divine.traveller.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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

    val hotelBookingsByDay by viewModel.hotelBookingsByDay.collectAsState(emptyMap())

    //TODO: Show days without hotel bookings only once when the calendar is first displayed
//    val context = LocalContext.current

//    LaunchedEffect(hotelBookingsByDay) {
//        val days = viewModel.daysWithoutBookings()
//        if (days.isNotEmpty()) {
//            val message = days.joinToString(", ") { it.toString() }
//            Toast.makeText(context, "Days without hotel: $message", Toast.LENGTH_LONG).show()
//        }
//    }

    // Color palette (cycled)
    val palette = listOf(
        Color(0xFFEF9A9A).copy(alpha = 0.6f),
        Color(0xFFA5D6A7).copy(alpha = 0.6f),
        Color(0xFF90CAF9).copy(alpha = 0.6f),
        Color(0xFFFFF59D).copy(alpha = 0.6f),
        Color(0xFFCE93D8).copy(alpha = 0.6f)
    )

    // Assign a color per distinct hotel (by hotelId or bookingId)
    val bookingColorMap = remember(hotelBookingsByDay) {
        val map = LinkedHashMap<Long, Color>()
        var idx = 0
        hotelBookingsByDay
            .values
            .flatten()
            .distinctBy { it.id }  // adapt property if different
            .forEach { booking ->
                map[booking.id] = palette[idx % palette.size]
                idx++
            }
        map
    }

    fun dayHighlight(date: LocalDate) =
        computeDayHighlightInfo(
            date = date,
            bookingsByDay = hotelBookingsByDay,
            colorMap = bookingColorMap
        )

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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
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
                        if (date != null) {
                            val hi = dayHighlight(date)
                            val backgroundSpec = dayRangeBackground(hi)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = backgroundSpec.brush,
                                        shape = backgroundSpec.shape
                                    )
                                    .padding(PaddingValues(horizontal = 4.dp))
                            ) {
                                CalendarDayItem(
                                    date = date,
                                    items = itemsPerDay,
                                    isSelected = date == selectedDay,
                                    isCurrentMonth = date.month == currentMonth.month,
                                    isTripDay = tripDates.contains(date),
                                    isToday = date == LocalDate.now(),
                                    onClick = { onClickDay(date) },
                                    compact = false,
                                )
                            }
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
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(tripDaysInMonth) { date ->
                        val hi = dayHighlight(date)
                        val backgroundSpec = dayRangeBackground(hi)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = backgroundSpec.brush,
                                    shape = backgroundSpec.shape
                                )
                                .padding(PaddingValues(horizontal = 4.dp))
                        ) {
                            CalendarDayItem(
                                date = date,
                                items = itemsPerDay,
                                isSelected = date == selectedDay,
                                isCurrentMonth = date.month == currentMonth.month,
                                isTripDay = tripDates.contains(date),
                                isToday = date == LocalDate.now(),
                                onClick = { onClickDay(date) },
                                compact = true,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun dayRangeBackground(info: DayHighlightInfo): BackgroundSpec {
    if (info.colors.isEmpty()) return BackgroundSpec(
        brush = Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent)),
        shape = RoundedCornerShape(50.dp)
    )

    val shape: Shape = RoundedCornerShape(
        topStart = if (info.leftContinues) 0.dp else 50.dp,
        bottomStart = if (info.leftContinues) 0.dp else 50.dp,
        topEnd = if (info.rightContinues) 0.dp else 50.dp,
        bottomEnd = if (info.rightContinues) 0.dp else 50.dp
    )

    val brush = if (info.isGradient && info.colors.size >= 2) {
        Brush.horizontalGradient(info.colors)
    } else {
        Brush.horizontalGradient(listOf(info.colors.first(), info.colors.first()))
    }

    return BackgroundSpec(brush = brush, shape = shape)
}

data class BackgroundSpec(val brush: Brush, val shape: Shape)