package com.divine.traveller.data.model

import com.divine.traveller.data.entity.HotelStatus
import java.util.Date

data class HotelModel(
    val id: Long,
    val tripId: Long,
    val name: String,
    val address: String,
    val checkInDate: Date,
    val checkOutDate: Date,
    val bookingReference: String?,
    val placeId: String?,
    val status: HotelStatus
)