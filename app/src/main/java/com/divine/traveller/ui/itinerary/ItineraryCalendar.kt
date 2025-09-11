package com.divine.traveller.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.model.ItineraryItemModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ItineraryCalendar(
    modifier: Modifier = Modifier,
    itemsPerDay: List<ItineraryItemModel>,
    selectedDay: LocalDate? = null,
    tripDates: Set<LocalDate> = emptySet(),
    isExpanded: Boolean = true,
    onClickDay: (LocalDate) -> Unit = {},
    onToggleExpanded: () -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }


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

                    val monthDays = getMonthDays(currentMonth)
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
                val monthDays = getMonthDays(currentMonth)
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

@Composable
private fun CalendarDayItem(
    date: LocalDate,
    items: List<ItineraryItemModel>,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    isTripDay: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false
) {
    val itemSize = if (compact) 24.dp else 44.dp
    val fontSize = if (compact) 10.sp else 14.sp
    val dotSize = if (compact) 2.dp else 4.dp

    Box(
        modifier = Modifier
            .size(itemSize)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isTripDay && isCurrentMonth -> MaterialTheme.colorScheme.primaryContainer
                    isToday && isCurrentMonth -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isToday && !isSelected) 1.dp else 0.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = fontSize,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    isTripDay -> MaterialTheme.colorScheme.onPrimaryContainer
                    isToday -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Show category dots for items (only if not compact or has items)
//            if (items.isNotEmpty() && isCurrentMonth && (!compact || items.size <= 2)) {
//                Spacer(modifier = Modifier.height(1.dp))
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(1.dp)
//                ) {
//                    val categories =
//                        items.map { it.category }.distinct().take(if (compact) 2 else 3)
//                    categories.forEach { category ->
//                        Box(
//                            modifier = Modifier
//                                .size(dotSize)
//                                .clip(CircleShape)
//                                .background(getCategoryColor(category))
//                        )
//                    }
//                }
//            }
        }
    }
}

private fun getMonthDays(yearMonth: YearMonth): List<LocalDate?> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

    val days = mutableListOf<LocalDate?>()

    // Add empty cells for days before the first day of the month
    repeat(firstDayOfWeek) {
        days.add(null)
    }

    // Add all days of the month
    var currentDay = firstDayOfMonth
    while (!currentDay.isAfter(lastDayOfMonth)) {
        days.add(currentDay)
        currentDay = currentDay.plusDays(1)
    }

    // Fill remaining cells to complete the grid (6 rows * 7 days = 42 cells)
    while (days.size < 42) {
        days.add(null)
    }

    return days
}

private fun getCategoryColor(category: ItineraryCategory): Color {
    return when (category) {
        ItineraryCategory.FLIGHT -> Color(0xFF2196F3) // Blue
        ItineraryCategory.HOTEL -> Color(0xFF4CAF50) // Green
        ItineraryCategory.ACTIVITY -> Color(0xFFFF9800) // Orange
        ItineraryCategory.TRANSPORT -> Color(0xFF9C27B0) // Purple
        ItineraryCategory.MEAL -> Color(0xFFFF5722) // Red
        ItineraryCategory.OTHER -> Color(0xFF607D8B) // Blue Grey
    }
}