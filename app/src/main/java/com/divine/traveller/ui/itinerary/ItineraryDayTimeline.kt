package com.divine.traveller.ui.itinerary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.ui.composable.ItineraryItemCard
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

    Column(modifier = modifier) {
        Text("$day", modifier = Modifier.padding(16.dp))
        LazyColumn(
            state = lazyListState
        ) {
            items(itemsForDay) { item ->
                if (item.category == ItineraryCategory.FLIGHT) {
                    //TODO: FlightItemCard
                }
                if (item.category == ItineraryCategory.HOTEL) {
                    //TODO: HotelItemCard
                } else {
                    ItineraryItemCard(
                        item = item,
                        placesClient = viewModel.placesClient,
//                        onClick = {
//                            selectedItem = item
//                            selectedDate =
//                                item.dayDate
//                            showSheet = true
//                        }
                    )
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