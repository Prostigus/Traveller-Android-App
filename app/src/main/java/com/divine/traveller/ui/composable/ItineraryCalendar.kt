package com.divine.traveller.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.divine.traveller.model.ItineraryItemModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun ItineraryCalendar(
    modifier: Modifier = Modifier,
    itemsPerDay: List<Pair<LocalDate, List<ItineraryItemModel>>>,
    selectedDay: LocalDate? = null,
    onClickDay: (LocalDate) -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    // Get items grouped by date for easy lookup
    val itemsByDate = remember(itemsPerDay) {
        itemsPerDay.associate { it.first to it.second }
    }

    // Get all trip dates for highlighting
    val tripDates = remember(itemsPerDay) {
        itemsPerDay.map { it.first }.toSet()
    }

    // Auto-select today if it's in the trip dates and no day is selected
    LaunchedEffect(itemsPerDay, selectedDay) {
        if (selectedDay == null && tripDates.contains(LocalDate.now())) {
            onClickDay(LocalDate.now())
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
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
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Calendar grid
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

                // Get all days for the current month view
                val monthDays = getMonthDays(currentMonth)
                items(monthDays) { date ->
                    val items = itemsByDate[date] ?: emptyList()
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
                            onClick = { onClickDay(date) }
                        )
                    } else {
                        // Empty cell for spacing
//                        Spacer(modifier = Modifier.size(44.dp))
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
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
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
                width = if (isToday && !isSelected) 2.dp else 0.dp,
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
                fontSize = 14.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    isTripDay -> MaterialTheme.colorScheme.onPrimaryContainer
                    isToday -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Show category dots for items
            if (items.isNotEmpty() && isCurrentMonth) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    val categories = items.map { it.category }.distinct().take(3)
                    categories.forEach { category ->
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(getCategoryColor(category))
                        )
                    }
                }
            }
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