package com.divine.traveller.ui.trips

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.divine.traveller.R

@Composable
fun TripScreenNavBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* Current screen */ },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_calendar_today),
                    contentDescription = "Itinerary"
                )
            },
            label = { Text("Itinerary", fontSize = 12.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to flights */ },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.outline_flight),
                    contentDescription = "Flights"
                )
            },
            label = { Text("Flights", fontSize = 12.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to hotels */ },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.outline_hotel),
                    contentDescription = "Hotels"
                )
            },
            label = { Text("Hotels", fontSize = 12.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: Navigate to costs */ },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.outline_attach_money),
                    contentDescription = "Costs"
                )
            },
            label = { Text("Costs", fontSize = 12.sp) }
        )
    }
}