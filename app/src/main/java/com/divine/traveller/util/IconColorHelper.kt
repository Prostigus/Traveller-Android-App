package com.divine.traveller.util

import androidx.compose.ui.graphics.Color
import com.divine.traveller.R
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus

fun getCategoryIcon(category: ItineraryCategory): Int {
    return when (category) {
        ItineraryCategory.FLIGHT -> R.drawable.outline_flight
        ItineraryCategory.HOTEL -> R.drawable.outline_hotel
        ItineraryCategory.ACTIVITY -> R.drawable.baseline_location_pin
        ItineraryCategory.TRANSPORT -> R.drawable.outline_train
        ItineraryCategory.MEAL -> R.drawable.outline_food
        ItineraryCategory.OTHER -> R.drawable.outline_push_pin
    }
}

fun getCategoryColor(category: ItineraryCategory): Color {
    return when (category) {
        ItineraryCategory.FLIGHT -> Color(0xFF2196F3) // Blue
        ItineraryCategory.HOTEL -> Color(0xFF4CAF50) // Green
        ItineraryCategory.ACTIVITY -> Color(0xFFFF9800) // Orange
        ItineraryCategory.TRANSPORT -> Color(0xFF9C27B0) // Purple
        ItineraryCategory.MEAL -> Color(0xFFFF5722) // Red
        ItineraryCategory.OTHER -> Color(0xFF607D8B) // Blue Grey
    }
}

fun getStatusColor(status: ItineraryItemStatus): Color {
    return when (status) {
        ItineraryItemStatus.PENDING -> Color(0xFFFF9800) // Orange
        ItineraryItemStatus.COMPLETED -> Color(0xFF4CAF50) // Green
        ItineraryItemStatus.CANCELLED -> Color(0xFFF44336) // Red
        ItineraryItemStatus.NONE -> Color(0xFF000000)
    }
}