package com.divine.traveller.data.model

import com.divine.traveller.data.entity.FlightStatus
import java.time.ZonedDateTime

data class FlightModel(
    val id: Long,
    val tripId: Long,
    val airline: String,
    val flightNumber: String,
    val departureAirport: String,
    val departureIATA: String,
    val arrivalAirport: String,
    val arrivalIATA: String,
    val departureDateTime: ZonedDateTime,
    val arrivalDateTime: ZonedDateTime,
    val status: FlightStatus
)