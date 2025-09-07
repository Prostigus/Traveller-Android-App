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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    includedTypes: List<String> = emptyList(),
//    locationRestriction: String = "",
) {
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Efficient autocomplete search with debouncing
    LaunchedEffect(value) {
        if (value.length >= 2) {
            delay(300) // Debounce
            isLoading = true
            Log.d("PlacesAutocomplete", "Searching for predictions with query: $value")

            try {
                val builder = FindAutocompletePredictionsRequest.builder()
                    .setQuery(value)

                if (includedTypes.isNotEmpty()) {
                    builder.setTypesFilter(includedTypes)
                }
                //TODO: Implement location restriction properly
//                if(!locationRestriction.isBlank()){
//                    builder.setLocationRestriction(null)
//                }

                val request = builder.build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        predictions = response.autocompletePredictions
                        Log.d(
                            "PlacesAutocomplete",
                            "Found ${predictions.size} predictions for query: $value"
                        )
                        expanded = predictions.isNotEmpty()
                        isLoading = false
                    }
                    .addOnFailureListener { exception ->
                        Log.d(
                            "PlacesAutocomplete",
                            "Error fetching predictions: ${exception.message}"
                        )
                        predictions = emptyList()
                        expanded = false
                        isLoading = false
                    }
            } catch (e: Exception) {
                Log.d("PlacesAutocomplete", "Error fetching predictions: ${e.message}")
                predictions = emptyList()
                expanded = false
                isLoading = false
            }
        } else {
            predictions = emptyList()
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
                                    predictions = emptyList()
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

            if (expanded && predictions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    predictions.forEach { prediction ->
                        DropdownMenuItem(
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = prediction.getPrimaryText(null).toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    prediction.getSecondaryText(null)?.let { secondaryText ->
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = secondaryText.toString(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            },
                            onClick = {
                                coroutineScope.launch {
                                    // Fetch the full place details using the prediction's place ID
                                    val placeFields = listOf(
                                        Place.Field.ID,
                                        Place.Field.DISPLAY_NAME,
                                        Place.Field.FORMATTED_ADDRESS,
                                        Place.Field.LOCATION
                                    )
                                    val placeRequest = FetchPlaceRequest.newInstance(
                                        prediction.placeId,
                                        placeFields
                                    )

                                    placesClient.fetchPlace(placeRequest)
                                        .addOnSuccessListener { response ->
                                            val place = response.place
                                            onValueChange(
                                                place.displayName ?: prediction.getPrimaryText(null)
                                                    .toString()
                                            )
                                            onPlaceSelected(place)
                                            expanded = false
                                            predictions = emptyList()
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e(
                                                "PlacesAutocomplete",
                                                "Error fetching place details: ${exception.message}"
                                            )
                                            // Fallback: use prediction text
                                            onValueChange(
                                                prediction.getPrimaryText(null).toString()
                                            )
                                            expanded = false
                                            predictions = emptyList()
                                        }
                                }
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