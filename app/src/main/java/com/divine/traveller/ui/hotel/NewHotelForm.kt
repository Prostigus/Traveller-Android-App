package com.divine.traveller.ui.hotel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divine.traveller.R
import com.divine.traveller.data.repository.PlaceRepository
import com.divine.traveller.data.statemodel.NewHotelState
import com.divine.traveller.ui.composable.FormFieldWithIcon
import com.divine.traveller.ui.composable.LabeledDateTimeBox
import com.divine.traveller.ui.composable.PlacesAutocompleteTextField
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import java.time.LocalDateTime

@Composable
fun NewHotelForm(
    state: NewHotelState,
    modifier: Modifier = Modifier,
    onBookingReferenceChange: (String) -> Unit,
    onCheckInLocalDateChange: (LocalDateTime) -> Unit,
    onCheckOutLocalDateChange: (LocalDateTime) -> Unit,
    onPlaceChange: (Place) -> Unit,
    placeRepository: PlaceRepository
) {
    Spacer(modifier = Modifier.height(8.dp))

    Column {
        Text(
            text = "Address",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PlacesAutocompleteTextField(
            onPlaceSelected = { selectedPlace ->
                onPlaceChange(selectedPlace)
            },
            placeRepository = placeRepository,
            modifier = Modifier.fillMaxWidth(),
            label = "Accommodations Address",
            placeholder = "Fairmont Hotels and Resorts",
            includedTypes = listOf(PlaceTypes.LODGING, PlaceTypes.STREET_ADDRESS),
            text = state.place?.displayName ?: "",
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        LabeledDateTimeBox(
            label = "Check-in Date/Time",
            onDateTimeChanged = { localDateTime ->
                onCheckInLocalDateChange(localDateTime)
            },
            dateTime = state.checkInLocalDate,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(16.dp))
        LabeledDateTimeBox(
            label = "Check-out Date/Time",
            onDateTimeChanged = { localDateTime ->
                onCheckOutLocalDateChange(localDateTime)
            },
            dateTime = state.checkOutLocalDate,
            modifier = Modifier.weight(1f)
        )
    }

    FormFieldWithIcon(
        value = state.bookingReference,
        onValueChange = { onBookingReferenceChange(it) },
        label = "Booking Reference (Optional)",
        placeholder = "e.g. ABC123",
        drawableRes = R.drawable.outline_bookmark
    )
}