package com.divine.traveller.ui.composable

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.delay

data class PlaceResult(
    val placeId: String,
    val name: String,
    val address: String,
    val latLng: LatLng
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesAutocompleteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onPlaceSelected: (Place) -> Unit,
    placesClient: PlacesClient,
    modifier: Modifier = Modifier,
    label: String = "Search location",
    placeholder: String = "Enter a location...",
    includedType: String = "all",
//    locationRestriction: String = "",
) {
    var suggestions by remember { mutableStateOf<List<Place>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Efficient autocomplete search with debouncing
    LaunchedEffect(value) {
        if (value.length >= 2) {
            delay(300) // Debounce
            isLoading = true
            Log.d("PlacesAutocomplete", "Searching for predictions with query: $value")

            try {
                val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LOCATION)
                val builder = SearchByTextRequest.builder(value, placeFields)
                    .setMaxResultCount(6)

                if (includedType != "all") {
                    builder.setIncludedType(includedType)
                }
                //TODO: Implement location restriction properly
//                if(!locationRestriction.isBlank()){
//                    builder.setLocationRestriction(null)
//                }

                val searchRequest = builder.build()

                placesClient.searchByText(searchRequest)
                    .addOnSuccessListener { response ->
                        suggestions = response.places
                        Log.d("PlacesAutocomplete", "Found ${suggestions.size} suggestions for query: $value")
                        expanded = suggestions.isNotEmpty()
                        isLoading = false
                    }
                    .addOnFailureListener { exception ->
                        Log.d("PlacesAutocomplete", "Error fetching suggestions: ${exception.message}")
                        suggestions = emptyList()
                        expanded = false
                        isLoading = false
                    }
            } catch (e: Exception) {
                Log.d("PlacesAutocomplete", "Error fetching suggestions: ${e.message}")
                suggestions = emptyList()
                expanded = false
                isLoading = false
            }
        } else {
            suggestions = emptyList()
            expanded = false
            isLoading = false
        }
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        value.isNotEmpty() -> {
                            IconButton(
                                onClick = {
                                    onValueChange("")
                                    suggestions = emptyList()
                                    expanded = false
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                },
                singleLine = true
            )

            if (expanded && suggestions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    suggestions.forEach { place ->
                        DropdownMenuItem(
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = place.name ?: "Unknown",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    place.address?.let { address ->
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = address,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            },
                            onClick = {
                                val selectedPlace = place
                                onValueChange(selectedPlace.displayName)
                                onPlaceSelected(selectedPlace)
                                expanded = false
                                suggestions = emptyList()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}