package com.divine.traveller.ui.composable.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import com.divine.traveller.R
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.model.FlightModel
import com.divine.traveller.navigation.LocalNavController
import com.divine.traveller.navigation.Routes
import com.divine.traveller.ui.composable.DetailsActionRow
import com.divine.traveller.ui.composable.InfoRow
import com.divine.traveller.util.formatZonedDateTime
import com.divine.traveller.util.getCategoryColor
import com.divine.traveller.util.getCategoryIcon
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@Composable
fun ItineraryFlightItemCard(
    modifier: Modifier,
    tripId: Long,
    flight: FlightModel,
    onClick: () -> Unit = {}
) {

    var showDetails by remember { mutableStateOf(false) }
    val navController = LocalNavController.current

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
                flight.id.let { fid ->
                    navController?.navigate(Routes.EDIT_FLIGHT + "/$tripId/$fid")
                    showDetails = false
                }
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
                    val departureLabel =
                        flight.departureAirport?.iataCode?.takeIf { it.isNotBlank() }
                            ?: flight.departureAirport?.municipality
                            ?: ""
                    val arrivalLabel = flight.arrivalAirport?.iataCode?.takeIf { it.isNotBlank() }
                        ?: flight.arrivalAirport?.municipality
                        ?: ""

                    Text(
                        text = "Flight from $departureLabel to $arrivalLabel",
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

    // show when composed
    LaunchedEffect(Unit) {
        scope.launch { sheetState.show() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Flight to ${flight.arrivalAirport?.municipality}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${flight.airline} • ${flight.flightNumber}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            FlightRouteRow(
                departureIata = flight.departureAirport?.iataCode,
                departureMunicipality = flight.departureAirport?.municipality,
                arrivalIata = flight.arrivalAirport?.iataCode,
                arrivalMunicipality = flight.arrivalAirport?.municipality
            )
            DateRow(flight.departureDateTime, flight.arrivalDateTime)
            DetailsActionRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                onEdit = onEdit,
                onAttach = onAttach,
                onBudget = onBudget
            )
        }
    }
}

@Composable
private fun FlightRouteRow(
    modifier: Modifier = Modifier,
    departureIata: String?,
    departureMunicipality: String?,
    arrivalIata: String?,
    arrivalMunicipality: String?
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left (departure) column - weight to push center into middle
        AirportColumn(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            iata = departureIata,
            municipality = departureMunicipality,
            alignment = Alignment.Start
        )

        // Center cell: don't take weight so it stays exactly centered between the two weighted columns
        Box(
            modifier = Modifier.wrapContentWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(24.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(R.drawable.outline_flight),
                    contentDescription = "to",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(24.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                )
            }
        }


        AirportColumn(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            iata = arrivalIata,
            municipality = arrivalMunicipality,
            alignment = Alignment.End
        )
    }
}

@Composable
private fun AirportColumn(
    modifier: Modifier = Modifier,
    iata: String?,
    municipality: String?,
    alignment: Alignment.Horizontal
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = when (alignment) {
            Alignment.Start -> Alignment.Start
            Alignment.End -> Alignment.End
            else -> Alignment.CenterHorizontally
        }
    ) {
        iata?.let {
            Text(
                text = it,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        municipality?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DateRow(
    departureDateTime: ZonedDateTime,
    arrivalDateTime: ZonedDateTime
) {
    val dateFormatter =
        java.time.format.DateTimeFormatter.ofPattern("MMM d", java.util.Locale.getDefault())
    val timeFormatter =
        java.time.format.DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.getDefault())

    val items = listOf(
        "Date" to departureDateTime.format(dateFormatter),
        "Departure" to departureDateTime.format(timeFormatter),
        "Arrival" to arrivalDateTime.format(timeFormatter)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (label, value) ->
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}