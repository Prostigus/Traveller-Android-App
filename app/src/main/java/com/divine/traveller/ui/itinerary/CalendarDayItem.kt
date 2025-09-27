package com.divine.traveller.ui.itinerary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.divine.traveller.data.model.ItineraryItemModel
import java.time.LocalDate

@Composable
fun CalendarDayItem(
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