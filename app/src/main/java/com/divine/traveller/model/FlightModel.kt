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
    val departureDate: Long,
    val departureZoneId: String,
    val arrivalDate: Long,
    val arrivalZoneId: String,
    val status: FlightStatus
)

fun FlightModel.departureAsDate(): Date = Date(this.departureDate)

fun FlightModel.departureAsLocalDate(zoneId: String) =
    java.time.Instant.ofEpochMilli(this.departureDate).atZone(java.time.ZoneId.of(zoneId))
        .toLocalDate()

fun FlightModel.departureAsLocalDateTime(zoneId: String) =
    java.time.Instant.ofEpochMilli(this.departureDate).atZone(java.time.ZoneId.of(zoneId))
        .toLocalDateTime()

fun FlightModel.arrivalAsDate(): Date = Date(this.arrivalDate)

fun FlightModel.arrivalAsLocalDate(zoneId: String) =
    java.time.Instant.ofEpochMilli(this.arrivalDate).atZone(java.time.ZoneId.of(zoneId))
        .toLocalDate()

fun FlightModel.arrivalAsLocalDateTime(zoneId: String) =
    java.time.Instant.ofEpochMilli(this.arrivalDate).atZone(java.time.ZoneId.of(zoneId))
        .toLocalDateTime()