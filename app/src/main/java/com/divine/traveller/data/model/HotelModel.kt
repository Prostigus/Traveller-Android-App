package com.divine.traveller.data.model

import com.divine.traveller.data.entity.HotelStatus
import java.time.ZonedDateTime

data class HotelModel(
    val id: Long,
    val tripId: Long,
    val name: String,
    val address: String?,
    val checkInDate: ZonedDateTime,
    val checkOutDate: ZonedDateTime,
    val bookingReference: String?,
    val placeId: String?,
    val status: HotelStatus
)