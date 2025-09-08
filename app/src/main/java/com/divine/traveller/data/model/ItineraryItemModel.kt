package com.divine.traveller.data.model

import android.os.Parcelable
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.ZonedDateTime

@Parcelize
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
    val hotelId: Long? = null,
    val flightId: Long? = null,
    val dayDate: LocalDate? = null,
    val orderIndex: Long = 0L
) : Parcelable