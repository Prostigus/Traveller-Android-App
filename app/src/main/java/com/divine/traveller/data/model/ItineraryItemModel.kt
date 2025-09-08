package com.divine.traveller.data.model

import android.os.Parcelable
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemStatus
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Parcelize
data class ItineraryItemModel(
    val id: Long = 0,
    val tripId: Long,
    val title: String = "",
    val description: String? = null,
    val placeId: String? = null,
    val viewType: String? = null,
    val startDateTime: Long,
    val endDateTime: Long,
    val category: ItineraryCategory,
    val status: ItineraryItemStatus,
    val hotelId: Long? = null,
    val flightId: Long? = null
) : Parcelable

fun ItineraryItemModel.startAsDate(): Date = Date(this.startDateTime)

fun ItineraryItemModel.startAsLocalDate(zone: ZoneId): LocalDate =
    Instant.ofEpochMilli(this.startDateTime).atZone(zone).toLocalDate()

fun ItineraryItemModel.startAsLocalDateTime(zone: ZoneId): LocalDateTime =
    Instant.ofEpochMilli(this.startDateTime).atZone(zone).toLocalDateTime()

fun ItineraryItemModel.endAsDate(): Date = Date(this.endDateTime)

fun ItineraryItemModel.endAsLocalDate(zone: ZoneId): LocalDate =
    Instant.ofEpochMilli(this.endDateTime).atZone(zone).toLocalDate()

fun ItineraryItemModel.endAsLocalDateTime(zone: ZoneId): LocalDateTime =
    Instant.ofEpochMilli(this.endDateTime).atZone(zone).toLocalDateTime()