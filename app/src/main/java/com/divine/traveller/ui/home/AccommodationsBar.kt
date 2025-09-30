package com.divine.traveller.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.divine.traveller.R
import com.divine.traveller.data.viewmodel.HotelsByDay
import com.divine.traveller.ui.composable.card.ItineraryHotelItemCard
import java.time.LocalDate

@Composable
fun AccommodationsBar(
    selectedDay: LocalDate,
    hotelBookingsByDay: Map<LocalDate, HotelsByDay>,
    tripId: Long,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Flatten distinct hotels across days
    val hotels = remember(selectedDay, hotelBookingsByDay) {
        hotelBookingsByDay[selectedDay]?.hotels ?: emptyList()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.outline_hotel),
                        contentDescription = "Accommodation",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (hotels.isEmpty()) "No accommodation" else "Accommodation (${hotels.size})",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier
                            .rotate(rotation)
                            .size(24.dp)
                    )
                }
            }

            if (expanded && hotels.isNotEmpty()) {
                LazyRow {
                    items(hotels, key = { it.id }) { hotel ->
                        ItineraryHotelItemCard(
                            modifier = Modifier.widthIn(min = 200.dp, max = 350.dp),
                            tripId = tripId,
                            hotel = hotel,
                            onClick = { /* navigation handled by card itself */ }
                        )
                    }
                }
            }
        }
    }
}