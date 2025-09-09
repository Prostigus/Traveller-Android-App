package com.divine.traveller.ui.composable

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.divine.traveller.R
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import com.divine.traveller.data.model.ItineraryItemModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import java.time.ZonedDateTime
import java.util.Locale

@Composable
fun ItineraryItemCard(
    item: ItineraryItemModel,
    placesClient: PlacesClient,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var placeDetails by remember { mutableStateOf<Place?>(null) }
    val context = LocalContext.current

    // Fetch place details if googlePlaceLink is available
    LaunchedEffect(item.placeId) {
        if (!item.placeId.isNullOrEmpty()) {
            val placeId = item.placeId

            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS
            )

//            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
//            placesClient.fetchPlace(request)
//                .addOnSuccessListener { response ->
//                    placeDetails = response.place
//                }
//                .addOnFailureListener { exception ->
//                    // Handle error
//                }
        }
    }

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
                        .background(getCategoryColor(item.category).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(getCategoryIcon(item.category)),
                        contentDescription = item.category.displayName,
                        tint = getCategoryColor(item.category),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Title and time
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
//                        text = formatZonedDateTime(item.startDateTime),
                        text = item.orderIndex.toString(),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(getStatusColor(item.status).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = getStatusColor(item.status)
                    )
                }
            }

            // Description (if available)
            if (!item.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            // Place details and map (if available)
            placeDetails?.let { place ->
                Spacer(modifier = Modifier.height(12.dp))

                // Place info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = place.name ?: "Unknown location",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        place.address?.let { address ->
                            Text(
                                text = address,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Rating (if available)
                    place.rating?.let { rating ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFB000),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = String.format("%.1f", rating),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Toggle map visibility
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = if (expanded) "Hide Map" else "Show Map",
                        fontSize = 12.sp
                    )
                }

                // Map (if expanded)
                if (expanded && place.latLng != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    GoogleMapView(
                        latLng = place.latLng!!,
                        placeName = place.name ?: "Location",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleMapView(
    latLng: LatLng,
    placeName: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                onCreate(null)
                getMapAsync { googleMap ->
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(placeName)
                    )
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                    )
                    googleMap.uiSettings.apply {
                        isZoomControlsEnabled = true
                        isScrollGesturesEnabled = true
                        isZoomGesturesEnabled = true
                    }
                }
            }
        }
    )
}

private fun getCategoryIcon(category: ItineraryCategory): Int {
    return when (category) {
        ItineraryCategory.FLIGHT -> R.drawable.outline_flight
        ItineraryCategory.HOTEL -> R.drawable.outline_hotel
        ItineraryCategory.ACTIVITY -> R.drawable.baseline_location_pin
        ItineraryCategory.TRANSPORT -> R.drawable.outline_train
        ItineraryCategory.MEAL -> R.drawable.outline_food
        ItineraryCategory.OTHER -> R.drawable.outline_push_pin
    }
}

private fun getCategoryColor(category: ItineraryCategory): Color {
    return when (category) {
        ItineraryCategory.FLIGHT -> Color(0xFF2196F3) // Blue
        ItineraryCategory.HOTEL -> Color(0xFF4CAF50) // Green
        ItineraryCategory.ACTIVITY -> Color(0xFFFF9800) // Orange
        ItineraryCategory.TRANSPORT -> Color(0xFF9C27B0) // Purple
        ItineraryCategory.MEAL -> Color(0xFFFF5722) // Red
        ItineraryCategory.OTHER -> Color(0xFF607D8B) // Blue Grey
    }
}

private fun getStatusColor(status: com.divine.traveller.data.entity.ItineraryItemStatus): Color {
    return when (status) {
        ItineraryItemStatus.PENDING -> Color(0xFFFF9800) // Orange
        ItineraryItemStatus.COMPLETED -> Color(0xFF4CAF50) // Green
        ItineraryItemStatus.CANCELLED -> Color(0xFFF44336) // Red
        ItineraryItemStatus.NONE -> Color(0xFF000000)
    }
}

private fun formatZonedDateTime(date: ZonedDateTime): String {
    val formatter =
        java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    return date.format(formatter)
}


private fun extractPlaceIdFromLink(googlePlaceLink: String): String? {
    // Extract place ID from Google Maps URL
    // Format: https://maps.google.com/?cid=<place_id> or similar
    return try {
        when {
            googlePlaceLink.contains("place_id=") -> {
                googlePlaceLink.substringAfter("place_id=").substringBefore("&")
            }

            googlePlaceLink.contains("cid=") -> {
                googlePlaceLink.substringAfter("cid=").substringBefore("&")
            }

            else -> null
        }
    } catch (e: Exception) {
        null
    }
}