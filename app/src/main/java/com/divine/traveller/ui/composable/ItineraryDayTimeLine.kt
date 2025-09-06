package com.divine.traveller.ui.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.model.ItineraryItemModel
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun ItineraryDayTimeLine(
    modifier: Modifier = Modifier,
    itemsForDay: List<ItineraryItemModel>,
    tripId: Long,
    timeZone: ZoneId,
    viewModel: ItineraryViewModel,
    lazyListState: LazyListState
    )
{
    var showSheet by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }


    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        items(itemsForDay) { item ->
            ItineraryItemCard(
                item = item,
                placesClient = viewModel.placesClient,
                onClick = { /* Handle item click if needed */ }
            )
        }
    }


    AddItineraryItemBottomSheet(
        visible = showSheet,
        selectedDateTime = selectedDateTime,
        timeZone = timeZone,
        tripId = tripId,
        viewModel = viewModel,
        onDismiss = {
            showSheet = false
            selectedDateTime = null
        },
    )
}