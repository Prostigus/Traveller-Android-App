package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "flights")
data class FlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val airline: String,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: Date,
    val arrivalTime: Date,
    val status: FlightStatus
)

enum class FlightStatus {
    SCHEDULED,
    ON_TIME,
    DELAYED,
    CANCELLED,
    LANDED
}