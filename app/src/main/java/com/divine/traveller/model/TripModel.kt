package com.divine.traveller.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TripModel(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val destination: String,
    val startDateUtcMillis: Long,
    val endDateUtcMillis: Long,
    val budget: Double? = null,
    val currency: String = "USD",
    val imageUrl: String? = null,
    val isCompleted: Boolean = false,
    val createdAtUtcMillis: Long = System.currentTimeMillis(),
    val updatedAtUtcMillis: Long = System.currentTimeMillis(),
    val destinationZoneIdString: String
) : Parcelable

val TripModel.dateRange: String
    get() {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d")
        val start = java.time.Instant.ofEpochMilli(startDateUtcMillis)
            .atZone(java.time.ZoneId.of(destinationZoneIdString))
            .toLocalDate()
        val end = java.time.Instant.ofEpochMilli(endDateUtcMillis)
            .atZone(java.time.ZoneId.of(destinationZoneIdString))
            .toLocalDate()
        return "${start.format(formatter)} - ${end.format(formatter)}"
    }

val TripModel.duration: Long
    get() {
        val diffInMillis = endDateUtcMillis - startDateUtcMillis
        return (diffInMillis / (1000 * 60 * 60 * 24)) + 1 // +1 to include both start and end days
    }