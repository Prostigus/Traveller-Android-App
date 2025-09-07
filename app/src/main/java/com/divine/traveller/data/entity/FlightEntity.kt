package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flights")
data class FlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val airline: String,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureDate: Long,
    val departureZoneId: String,
    val arrivalDate: Long,
    val arrivalZoneId: String,
    val status: FlightStatus
)

enum class FlightStatus {
    SCHEDULED,
    ON_TIME,
    DELAYED,
    CANCELLED,
    LANDED
}