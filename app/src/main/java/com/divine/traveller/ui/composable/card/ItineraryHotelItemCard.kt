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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.model.HotelModel
import com.divine.traveller.data.viewmodel.HotelViewModel
import com.divine.traveller.navigation.LocalNavController
import com.divine.traveller.navigation.Routes
import com.divine.traveller.ui.composable.DetailsActionRow
import com.divine.traveller.ui.composable.InfoRow
import com.divine.traveller.util.getCategoryColor
import com.divine.traveller.util.getCategoryIcon
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ItineraryHotelItemCard(
    modifier: Modifier,
    tripId: Long,
    hotel: HotelModel,
    onClick: () -> Unit,
    viewModel: HotelViewModel = hiltViewModel()
) {

    var showDetails by remember { mutableStateOf(false) }
    val navController = LocalNavController.current

    HotelItemCard(
        modifier = modifier,
        hotel = hotel,
        onClick = {
            showDetails = true
            onClick()
        }
    )

    if (showDetails) {
        HotelDetailsModalSheet(
            hotel = hotel,
            onDismiss = { showDetails = false },
            onEdit = {
                hotel.id.let { hid ->
                    navController?.navigate(Routes.EDIT_HOTEL + "/$tripId/$hid")
                    showDetails = false
                }
            },
            onAttach = {
                // handle attachment
            },
            onBudget = {
                // handle budget
            },
            onDelete = {
                viewModel.delete(hotel)
            }
        )
    }
}

@Composable
fun HotelItemCard(
    modifier: Modifier,
    hotel: HotelModel,
    onClick: () -> Unit
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Header row with category icon and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getCategoryColor(ItineraryCategory.HOTEL).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(getCategoryIcon(ItineraryCategory.HOTEL)),
                        contentDescription = hotel.name,
                        tint = getCategoryColor(ItineraryCategory.HOTEL),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.End),
                    text = hotel.name,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            HotelCheckInCheckOutRow(
                checkIn = hotel.checkInDate,
                checkOut = hotel.checkOutDate
            )
            hotel.address.let {
                if (it?.isNotBlank() ?: false) {
                    InfoRow("Address", it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsModalSheet(
    hotel: HotelModel,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onAttach: () -> Unit,
    onBudget: () -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

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
                    text = hotel.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    painter = painterResource(getCategoryIcon(ItineraryCategory.HOTEL)),
                    contentDescription = hotel.name,
                    tint = getCategoryColor(ItineraryCategory.HOTEL),
                    modifier = Modifier.size(24.dp)
                )
            }
            HotelCheckInCheckOutRow(
                checkIn = hotel.checkInDate,
                checkOut = hotel.checkOutDate
            )
            DetailsActionRow(
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                onEdit = onEdit,
                onAttach = onAttach,
                onBudget = onBudget,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun HotelCheckInCheckOutRow(
    modifier: Modifier = Modifier,
    checkIn: ZonedDateTime,
    checkOut: ZonedDateTime
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        DateColumn(
            modifier = Modifier.weight(1f),
            label = "Check-in",
            dateTime = checkIn,
            alignment = Alignment.Start
        )
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
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
        DateColumn(
            modifier = Modifier.weight(1f),
            label = "Check-out",
            dateTime = checkOut,
            alignment = Alignment.End
        )
    }
}

@Composable
private fun DateColumn(
    modifier: Modifier = Modifier,
    label: String,
    dateTime: ZonedDateTime,
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
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = dateTime.format(DateTimeFormatter.ofPattern("MMM dd")),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (label == "Check-in") "From " + dateTime.format(DateTimeFormatter.ofPattern("hh:mm a")) else "By " + dateTime.format(
                DateTimeFormatter.ofPattern("hh:mm a")
            ),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}