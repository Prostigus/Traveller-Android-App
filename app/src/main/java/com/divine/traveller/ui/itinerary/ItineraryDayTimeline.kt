package com.divine.traveller.ui.itinerary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.ui.composable.ItineraryItemCard
import java.time.ZonedDateTime

@Composable
fun ItineraryDayTimeline(
    modifier: Modifier = Modifier,
    day: ZonedDateTime,
    itemsForDay: List<ItineraryItemModel>,
    tripId: Long,
    viewModel: ItineraryViewModel,
    lazyListState: LazyListState
) {
    var showSheet by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf<ZonedDateTime?>(null) }
    var selectedItem by remember { mutableStateOf<ItineraryItemModel?>(null) }


    Column(modifier = modifier) {
        Text("${day.toLocalDate()}", modifier = Modifier.padding(16.dp))
        LazyColumn(
            state = lazyListState
        ) {
            items(itemsForDay) { item ->
                ItineraryItemCard(
                    item = item,
                    placesClient = viewModel.placesClient,
                    onClick = {
                        selectedItem = item
                        selectedDateTime =
                            item.startDateTime
                        showSheet = true
                    }
                )
            }
        }

        Button(
            onClick = {
                showSheet = true
                selectedDateTime = day
                selectedItem = null
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Add Itinerary Item")
        }
    }


    ItineraryItemBottomSheet(
        visible = showSheet,
        selectedDateTime = selectedDateTime,
        tripId = tripId,
        viewModel = viewModel,
        selectedItem = selectedItem,
        onDismiss = {
            showSheet = false
            selectedDateTime = null
            selectedItem = null
        },
    )
}