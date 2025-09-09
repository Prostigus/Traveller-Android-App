package com.divine.traveller.ui.itinerary

import androidx.compose.foundation.layout.Column
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
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.ui.composable.ItineraryItemCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate

@Composable
fun ItineraryDayTimeline(
    modifier: Modifier = Modifier,
    day: LocalDate,
    itemsForDay: List<ItineraryItemModel>,
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

    Column(modifier = modifier) {
        // use the reorderable state's internal list state so drag events are handled
        LazyColumn(
            state = lazyListState
        ) {
            items(list, key = { it.id }) { item ->
                ReorderableItem(reorderableLazyListState, key = item.id) { isDragging ->

                    LaunchedEffect(isDragging) {
                        if (!isDragging) {
                            viewModel.reorderItemsForDay(tripId, day, list.map { it.id })
                        }
                    }

                    if (item.category == ItineraryCategory.FLIGHT) {

                    }
                    if (item.category == ItineraryCategory.HOTEL) {

                    } else {
                        ItineraryItemCard(
                            item = item,
                            placesClient = viewModel.placesClient,
                            modifier = Modifier
                                .longPressDraggableHandle()
                        )
                    }
                }
            }
        }
    }
}

private fun categoryPriority(category: ItineraryCategory): Int = when (category) {
    ItineraryCategory.FLIGHT -> 0
    ItineraryCategory.HOTEL -> 1
    else -> 2
}