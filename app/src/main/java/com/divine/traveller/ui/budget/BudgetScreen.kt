package com.divine.traveller.ui.budget

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.viewmodel.ItineraryViewModel
import com.divine.traveller.navigation.Routes.BUDGET_DETAILS
import com.divine.traveller.ui.composable.ItineraryNavBar

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    tripId: Long,
    viewModel: ItineraryViewModel = hiltViewModel(),
    onTripCreated: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = { /* ... */ },
        bottomBar = {
            ItineraryNavBar(
                selectedScreen = BUDGET_DETAILS,
                onNavigate = onNavigate,
                tripId = tripId
            )
        }
    ) { paddingValues ->
        Text("Flight Screen for tripId: $tripId", modifier = modifier)
    }
}