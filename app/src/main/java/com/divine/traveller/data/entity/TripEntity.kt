package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
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
)