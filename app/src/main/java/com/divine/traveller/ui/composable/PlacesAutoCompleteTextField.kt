package com.divine.traveller.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesAutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onPlaceSelected: (String) -> Unit,
    placesClient: PlacesClient,
    modifier: Modifier = Modifier,
    label: String = "",
    filterList: List<String> = listOf(
        "country",
        "administrative_area_level_1",
        "locality",
        "sublocality",
        "natural_feature",
        "archipelago",
        "island"
    )
) {
    var suggestions by remember { mutableStateOf<List<Place>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Debounced search effect
    LaunchedEffect(value) {
        if (value.length >= 3) {
            delay(500) // Debounce for 500ms
            isLoading = true

            val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES)
            val searchByTextRequest = SearchByTextRequest.builder(value, placeFields)
                .setMaxResultCount(10) // Get more results to filter
                .setIncludedType("locality") // Cities
                .build()

            placesClient.searchByText(searchByTextRequest)
                .addOnSuccessListener { response ->
                    isLoading = false

                    val filteredPlaces = if (filterList.isNotEmpty()) {
                        response.places.filter { place ->
                            val types = place.placeTypes ?: emptyList()
                            types.any { type -> type in filterList }
                        }.take(6)
                    } else {
                        response.places.take(8)
                    }

                    suggestions = filteredPlaces
                    expanded = suggestions.isNotEmpty()
                    println("Places API Success: Found ${filteredPlaces.size} filtered suggestions")
                }
                .addOnFailureListener { exception ->
                    isLoading = false
                    expanded = false
                    suggestions = emptyList()
                    println("Places API Error: ${exception.message}")
                    exception.printStackTrace()
                }
        } else {
            suggestions = emptyList()
            expanded = false
            isLoading = false
        }
    }

    // Rest of your existing Column and UI code remains the same
    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location"
                        )
                    }
                }
            )

            if (expanded && suggestions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    suggestions.forEach { place ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = place.name ?: "",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = place.address ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                val selectedPlace = place.name ?: ""
                                onValueChange(selectedPlace)
                                onPlaceSelected(selectedPlace)
                                expanded = false
                                suggestions = emptyList()
                            }
                        )
                    }
                }
            }
        }
    }
}