package com.divine.traveller.ui.hotel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.statemodel.NewHotelStateModel
import com.divine.traveller.data.viewmodel.HotelViewModel
import com.divine.traveller.data.viewmodel.NewHotelCreationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHotelScreen(
    modifier: Modifier = Modifier,
    tripId: Long,
    hotelId: Long? = null,
    onHotelCreated: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    newHotelStateModel: NewHotelStateModel = hiltViewModel(),
    viewModel: HotelViewModel = hiltViewModel()
) {
    val state by newHotelStateModel.uiState.collectAsState()
    val creationState by viewModel.newHotelCreation.collectAsState()

    LaunchedEffect(hotelId) {
        if (hotelId != null) {
            val hotel = viewModel.getById(hotelId)
            if (hotel != null) {
                val place =
                    hotel.placeId?.let { viewModel.placeRepository.getPlace(it) }
                newHotelStateModel.replaceState(hotel, place)
            }
        }
    }

    LaunchedEffect(creationState) {
        if (creationState is NewHotelCreationState.Success) {
            onHotelCreated()
            viewModel.resetNewHotelCreation()
        }
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
                            text = if (hotelId != null) "Update Accommodation" else "Add Accommodation",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        // Empty space to center title
                        Spacer(modifier = Modifier.width(48.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Button(
                        onClick = {
                            if (state.checkInLocalDate != null && state.checkOutLocalDate != null && state.place != null) {
                                viewModel.createNewHotel(tripId, state)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = state.place != null
                                && state.checkInLocalDate != null
                                && state.checkOutLocalDate != null
                    ) {
                        Text(
                            if (hotelId != null) "Update Accommodation" else "Add Accommodation",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            NewHotelForm(
                state = state,
                onBookingReferenceChange = newHotelStateModel::setBookingReference,
                onPlaceChange = newHotelStateModel::setPlace,
                onCheckInLocalDateChange = newHotelStateModel::setCheckInLocalDate,
                onCheckOutLocalDateChange = newHotelStateModel::setCheckOutLocalDate,
                placeRepository = viewModel.placeRepository,
            )
        }
        if (creationState is NewHotelCreationState.Creating) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}