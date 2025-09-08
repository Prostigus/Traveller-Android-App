package com.divine.traveller.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Parcelize
data class TripModel(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val destination: String,
    val startDateTime: ZonedDateTime,
    val endDateTime: ZonedDateTime,
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
        val formatter = DateTimeFormatter.ofPattern("MMM d")
        val start = startDateTime.toLocalDate()
        val end = endDateTime.toLocalDate()
        return "${start.format(formatter)} - ${end.format(formatter)}"
    }

//val TripModel.duration: Long
//    get() {
//        val diffInMillis = endDateTime.toInstant().toEpochMilli() - startDateTime.toInstant().toEpochMilli()
//        return (diffInMillis / (1000 * 60 * 60 * 24)) + 1 // +1 to include both start and end days
//    }

val TripModel.duration: Long
    get() {
        return startDateTime.until(
            endDateTime,
            java.time.temporal.ChronoUnit.DAYS
        ) + 1 // +1 to include both start and end days
    }