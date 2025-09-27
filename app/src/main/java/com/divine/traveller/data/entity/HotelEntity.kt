package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val name: String,
    val address: String? = null,
    val checkInDate: ZonedDateTime,
    val checkOutDate: ZonedDateTime,
    val bookingReference: String? = null,
    val placeId: String?,
    val status: HotelStatus = HotelStatus.BOOKED,
)

enum class HotelStatus {
    BOOKED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED
}