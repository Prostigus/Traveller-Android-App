package com.divine.traveller.ui.flight

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
import com.divine.traveller.data.statemodel.NewFlightState
import com.divine.traveller.ui.composable.FormFieldWithIcon
import com.divine.traveller.ui.composable.LabeledDateTimeBox
import com.divine.traveller.ui.composable.PlacesAutocompleteTextField
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.PlacesClient
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun NewFlightForm(
    state: NewFlightState,
    modifier: Modifier = Modifier,
    onAirlineChange: (String) -> Unit,
    onFlightNumberChange: (String) -> Unit,
    onDeparturePlaceChange: (Place) -> Unit,
    onArrivalPlaceChange: (Place) -> Unit,
    onDepartureDateChange: (ZonedDateTime?) -> Unit,
    onArrivalDateChange: (ZonedDateTime?) -> Unit,
    onDepartureLocalDateChange: (LocalDateTime) -> Unit,
    onArrivalLocalDateChange: (LocalDateTime) -> Unit,
    placesClient: PlacesClient,
) {
    Spacer(modifier = Modifier.height(8.dp))

    FormFieldWithIcon(
        value = state.airline,
        onValueChange = { onAirlineChange(it) },
        label = "Airline",
        placeholder = "e.g.  Air Canada",
        drawableRes = R.drawable.outline_flight_takeoff
    )

    FormFieldWithIcon(
        value = state.flightNumber,
        onValueChange = { onFlightNumberChange(it) },
        label = "Flight Number",
        placeholder = "e.g.  AC837",
        drawableRes = R.drawable.outline_airplane_ticket
    )

    Column {
        Text(
            text = "From",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PlacesAutocompleteTextField(
            onPlaceSelected = { selectedPlace ->
                onDeparturePlaceChange(selectedPlace)
            },
            placesClient = placesClient,
            modifier = Modifier.fillMaxWidth(),
            label = "Departure Airport",
            placeholder = "YVR",
            includedTypes = listOf(PlaceTypes.AIRPORT),
        )
    }

    Column {
        Text(
            text = "To",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PlacesAutocompleteTextField(
            onPlaceSelected = { selectedPlace ->
                onArrivalPlaceChange(selectedPlace)
            },
            placesClient = placesClient,
            modifier = Modifier.fillMaxWidth(),
            label = "Arrival Airport",
            placeholder = "HND",
            includedTypes = listOf(PlaceTypes.AIRPORT),
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        LabeledDateTimeBox(
            label = "Departure Date/Time",
            onDateTimeChanged = { localDateTime ->
                onDepartureLocalDateChange(localDateTime)
            },
            dateTime = state.departureLocalDate,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        LabeledDateTimeBox(
            label = "Arrival Date/Time",
            onDateTimeChanged = { localDateTime ->
                onArrivalLocalDateChange(localDateTime)
            },
            dateTime = state.arrivalLocalDate,
            modifier = Modifier.weight(1f)
        )
    }

}