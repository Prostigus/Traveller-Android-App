package com.divine.traveller.ui.composable

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.divine.traveller.R
import com.divine.traveller.navigation.Routes

private data class NavItem(
    val route: String,
    val iconRes: Int,
    val label: String,
    val needsTripId: Boolean = true
)

@Composable
fun ItineraryNavBar(
    selectedScreen: String,
    onNavigate: (String) -> Unit,
    tripId: Long,
) {
    val items = listOf(
        NavItem(Routes.TRIP_DETAILS, R.drawable.baseline_calendar_today, "Itinerary"),
        NavItem(Routes.FLIGHT_DETAILS, R.drawable.outline_flight, "Flights"),
        NavItem(Routes.HOTEL_DETAILS, R.drawable.outline_hotel, "Hotels"),
        NavItem(Routes.BUDGET_DETAILS, R.drawable.outline_attach_money, "Costs")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { item ->
            val destination = if (item.needsTripId) "${item.route}/$tripId" else item.route
            NavigationBarItem(
                selected = selectedScreen == item.route,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, fontSize = 12.sp) }
            )
        }
    }
}