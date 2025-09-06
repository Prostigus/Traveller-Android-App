package com.divine.traveller.model

import android.os.Parcelable
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class ItineraryItemModel(
    val id: Long = 0,
    val tripId: Long,
    val title: String,
    val description: String?,
    val placeId: String?,
    val viewType: String?,
    val startDateTime: Date,
    val endDateTime: Date,
    val category: ItineraryCategory,
    val status: ItineraryItemStatus,
    val hotelId: Long? = null,
    val flightId: Long? = null
) : Parcelable