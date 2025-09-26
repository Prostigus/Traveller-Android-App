package com.divine.traveller.ui.composable.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.model.FlightModel
import com.divine.traveller.data.viewmodel.FlightViewModel
import com.divine.traveller.ui.composable.InfoRow
import com.divine.traveller.util.formatZonedDateTime
import com.divine.traveller.util.getCategoryColor
import com.divine.traveller.util.getCategoryIcon
import kotlinx.coroutines.launch

@Composable
fun ItineraryFlightItemCard(
    modifier: Modifier,
    flight: FlightModel,
    onClick: () -> Unit = {},
    viewModel: FlightViewModel = hiltViewModel()
) {

    var showDetails by remember { mutableStateOf(false) }

//    if (nullableFlight.value == null) {
//        Box(
//            modifier = modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//        return
//    }

//    val flight = nullableFlight.value!!
    FlightItemCard(
        modifier = modifier,
        flight = flight,
        onClick = {
            showDetails = true
            onClick()
        }
    )

    if (showDetails) {
        FlightDetailsModalSheet(
            flight = flight,
            onDismiss = { showDetails = false },
            onEdit = {
                showDetails = false
            },
            onAttach = {
                // handle attachment
            },
            onBudget = {
                // handle budget
            }
        )
    }
}

@Composable
fun FlightItemCard(
    modifier: Modifier,
    flight: FlightModel,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row with category icon and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category icon with background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getCategoryColor(ItineraryCategory.FLIGHT).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(getCategoryIcon(ItineraryCategory.FLIGHT)),
                        contentDescription = flight.flightNumber,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Flight from ${flight.departureAirport?.iataCode} to ${flight.arrivalAirport?.iataCode}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
            }

            Column(
                modifier = Modifier
                    .padding(top = 12.dp, start = 4.dp, end = 4.dp, bottom = 4.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow("Airline", flight.airline)
                InfoRow("Flight Number", flight.flightNumber)
                InfoRow("Departure", formatZonedDateTime(flight.departureDateTime))
                InfoRow("Arrival", formatZonedDateTime(flight.arrivalDateTime))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightDetailsModalSheet(
    flight: FlightModel,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onAttach: () -> Unit,
    onBudget: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // show when composed
    LaunchedEffect(Unit) {
        scope.launch { sheetState.show() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
//            Column(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .verticalScroll(scrollState)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.Top
//                ) {
//                    Column(modifier = Modifier.weight(1f)) {
//                        Text(
//                            text = if (flight.departureIATA.isNotEmpty() && flight.arrivalIATA.isNotEmpty())
//                                "Flight from ${flight.departureIATA} to ${flight.arrivalIATA}"
//                            else
//                                "Flight from ${flight.departureAirport} to ${flight.arrivalAirport}",
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 18.sp,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                        Text(
//                            text = "${flight.airline} • ${flight.flightNumber}",
//                            fontSize = 14.sp,
//                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//                        )
//                    }
//
//                    Row {
//                        IconButton(onClick = {
//                            scope.launch {
//                                sheetState.hide()
//                                onEdit()
//                            }
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.Edit,
//                                contentDescription = "Edit",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                        IconButton(onClick = onAttach) {
//                            Icon(
//                                painter = painterResource(R.drawable.outline_attach_file),
//                                contentDescription = "Attach",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                        IconButton(onClick = onBudget) {
//                            Icon(
//                                painter = painterResource(R.drawable.outline_attach_money),
//                                contentDescription = "Budget",
//                                tint = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//
//                Column(
//                    modifier = Modifier
//                        .padding(top = 12.dp)
//                        .fillMaxWidth(),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    InfoRow("Airline", flight.airline)
//                    InfoRow("Flight Number", flight.flightNumber)
//                    InfoRow("Departure", formatZonedDateTime(flight.departureDateTime))
//                    InfoRow("Arrival", formatZonedDateTime(flight.arrivalDateTime))
//                    InfoRow("Departure Airport", flight.departureAirport)
//                    InfoRow("Arrival Airport", flight.arrivalAirport)
//                }
//            }

    }
}