package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

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

fun TripEntity.startAsDate(): Date = Date(this.startDateUtcMillis)

fun TripEntity.startAsLocalDate(zone: ZoneId): LocalDate =
    java.time.Instant.ofEpochMilli(this.startDateUtcMillis).atZone(zone).toLocalDate()

fun TripEntity.startAsLocalDateTime(zone: ZoneId): LocalDateTime =
    java.time.Instant.ofEpochMilli(this.startDateUtcMillis).atZone(zone).toLocalDateTime()

fun TripEntity.endAsDate(): Date = Date(this.endDateUtcMillis)

fun TripEntity.endAsLocalDate(zone: ZoneId): LocalDate =
    java.time.Instant.ofEpochMilli(this.endDateUtcMillis).atZone(zone).toLocalDate()

fun TripEntity.endAsLocalDateTime(zone: ZoneId): LocalDateTime =
    java.time.Instant.ofEpochMilli(this.endDateUtcMillis).atZone(zone).toLocalDateTime()