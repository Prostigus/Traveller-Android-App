package com.divine.traveller.ui.hotel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.viewmodel.HotelViewModel
import com.divine.traveller.navigation.Routes.HOTEL_DETAILS
import com.divine.traveller.ui.composable.ItineraryNavBar
import com.divine.traveller.ui.composable.card.ItineraryHotelItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelScreen(
    modifier: Modifier = Modifier,
    tripId: Long,
    viewModel: HotelViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onNavigateToNewHotel: () -> Unit = {}
) {
    val hotelItems by viewModel.hotelItems.collectAsState()

    LaunchedEffect(tripId) {
        viewModel.tripId.value = tripId
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Accommodations",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = onNavigateToNewHotel,
                            modifier = Modifier
                                .background(
                                    color = Color.Transparent,
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add new accommodation",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            ItineraryNavBar(
                selectedScreen = HOTEL_DETAILS,
                onNavigate = onNavigate,
                tripId = tripId
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (hotelItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No accommodations added yet. Tap the + button to add a new accommodations.",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                items(hotelItems) { hotel ->
                    ItineraryHotelItemCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        tripId = tripId,
                        hotel = hotel,
                        onClick = {
                            // TODO: Navigate to flight details screen
                        }
                    )
                }
            }
        }
    }
}