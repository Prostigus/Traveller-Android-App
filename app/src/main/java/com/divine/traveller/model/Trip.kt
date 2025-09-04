package com.divine.traveller.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Trip(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val destination: String,
    val startDate: Date,
    val endDate: Date,
    val budget: Double? = null,
    val currency: String = "USD",
    val imageUrl: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable

val Trip.dateRange: String
    get() {
        val format = SimpleDateFormat("MMM d", Locale.getDefault())
        return "${format.format(startDate)} - ${format.format(endDate)}"
    }