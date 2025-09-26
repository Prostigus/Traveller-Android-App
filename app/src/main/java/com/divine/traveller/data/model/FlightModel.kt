package com.divine.traveller.data.model

import com.divine.traveller.data.entity.FlightStatus
import java.time.ZonedDateTime

data class FlightModel(
    val id: Long,
    val tripId: Long,
    val airline: String,
    val flightNumber: String,
    val departureAirport: AirportModel?,
    val arrivalAirport: AirportModel?,
    val departurePlaceId: String? = null,
    val arrivalPlaceId: String? = null,
    val departureDateTime: ZonedDateTime,
    val arrivalDateTime: ZonedDateTime,
    val status: FlightStatus
)