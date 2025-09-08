package com.divine.traveller.data.model

import com.divine.traveller.data.entity.FlightStatus
import java.time.Instant
import java.time.ZoneId
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
    Instant.ofEpochMilli(this.departureDate).atZone(ZoneId.of(zoneId))
        .toLocalDate()

fun FlightModel.departureAsLocalDateTime(zoneId: String) =
    Instant.ofEpochMilli(this.departureDate).atZone(ZoneId.of(zoneId))
        .toLocalDateTime()

fun FlightModel.arrivalAsDate(): Date = Date(this.arrivalDate)

fun FlightModel.arrivalAsLocalDate(zoneId: String) =
    Instant.ofEpochMilli(this.arrivalDate).atZone(ZoneId.of(zoneId))
        .toLocalDate()

fun FlightModel.arrivalAsLocalDateTime(zoneId: String) =
    Instant.ofEpochMilli(this.arrivalDate).atZone(ZoneId.of(zoneId))
        .toLocalDateTime()