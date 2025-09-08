package com.divine.traveller.ui.hotel

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.navigation.Routes.HOTEL_DETAILS
import com.divine.traveller.ui.composable.ItineraryNavBar

@Composable
fun HotelScreen(
    modifier: Modifier = Modifier,
    tripId: Long,
    viewModel: ItineraryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = { /* ... */ },
        bottomBar = {
            ItineraryNavBar(
                selectedScreen = HOTEL_DETAILS,
                onNavigate = onNavigate,
                tripId = tripId
            )
        }
    ) { paddingValues ->
        Text("Flight Screen for tripId: $tripId", modifier = modifier)
    }
}