package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
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
)