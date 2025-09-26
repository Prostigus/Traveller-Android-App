package com.divine.traveller.data.model

import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import java.time.LocalDate
import java.time.ZonedDateTime

data class ItineraryItemModel(
    val id: Long = 0,
    val tripId: Long,
    val title: String = "",
    val description: String? = null,
    val placeId: String? = null,
    val viewType: String? = null,
    val startDateTime: ZonedDateTime? = null,
    val endDateTime: ZonedDateTime? = null,
    val category: ItineraryCategory,
    val status: ItineraryItemStatus,
    val hotel: HotelModel? = null,
    val flight: FlightModel? = null,
    val dayDate: LocalDate? = null,
    val orderIndex: Long = 0L
)