package com.divine.traveller.data.model

import android.os.Parcelable
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class ItineraryItemModel(
    val id: Long = 0,
    val tripId: Long,
    val title: String = "",
    val description: String? = null,
    val placeId: String? = null,
    val viewType: String? = null,
    val startDateTime: ZonedDateTime,
    val endDateTime: ZonedDateTime,
    val category: ItineraryCategory,
    val status: ItineraryItemStatus,
    val hotelId: Long? = null,
    val flightId: Long? = null
) : Parcelable