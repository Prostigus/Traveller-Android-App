package com.divine.traveller.ui.itinerary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.viewmodel.HotelsByDay
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.ui.composable.card.ItineraryFlightItemCard
import com.divine.traveller.ui.composable.card.ItineraryItemCard
import com.divine.traveller.ui.home.AccommodationsBar
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate

@Composable
fun ItineraryDayTimeline(
    modifier: Modifier = Modifier,
    day: LocalDate,
    itemsForDay: List<ItineraryItemModel>,
    hotelBookingsByDay: Map<LocalDate, HotelsByDay>,
    tripId: Long,
    viewModel: ItineraryViewModel,
    lazyListState: LazyListState
) {

    // keep a mutable list that updates whenever itemsForDay changes
    var list by remember(itemsForDay) { mutableStateOf(itemsForDay) }

    // create reorderable state wrapping the provided lazyListState
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        list = list.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    val previousDragging = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // use the reorderable state's internal list state so drag events are handled
        LazyColumn(
            state = lazyListState
        ) {

            item {
                AccommodationsBar(
                    selectedDay = day,
                    hotelBookingsByDay = hotelBookingsByDay,
                    tripId = tripId,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            items(list, key = { it.id }) { item ->
                ReorderableItem(reorderableLazyListState, key = item.id) { isDragging ->

                    LaunchedEffect(isDragging) {
                        if (previousDragging.value && !isDragging) {
                            viewModel.reorderItemsForDay(tripId, day, list.map { it.id })
                        }
                        previousDragging.value = isDragging
                    }

                    if (item.category == ItineraryCategory.FLIGHT) {
                        ItineraryFlightItemCard(
                            tripId = tripId,
                            flight = item.flight!!,
                            modifier = Modifier
                                .longPressDraggableHandle()
                        )
                    }
                    if (item.category == ItineraryCategory.HOTEL) {

                    }
                    if (item.category != ItineraryCategory.FLIGHT && item.category != ItineraryCategory.HOTEL) {
                        ItineraryItemCard(
                            item = item,
                            modifier = Modifier
                                .longPressDraggableHandle()
                        )
                    }
                }
            }
        }
    }
}