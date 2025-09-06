package com.divine.traveller.model

import com.divine.traveller.data.entity.FlightHotelType
import java.util.Date

data class DocumentModel(
    val id: Long,
    val tripId: Long,
    val fileName: String,
    val fileUrl: String,
    val flightHotelId: Long?,
    val type: FlightHotelType?,
    val description: String?,
    val uploadedAt: Date
)