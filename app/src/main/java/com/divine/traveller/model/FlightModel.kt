package com.divine.traveller.model

import com.divine.traveller.data.entity.FlightStatus
import java.util.Date

data class FlightModel(
    val id: Long,
    val tripId: Long,
    val airline: String,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: Date,
    val arrivalTime: Date,
    val status: FlightStatus
)