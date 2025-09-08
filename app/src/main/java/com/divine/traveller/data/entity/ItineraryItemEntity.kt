package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.ZonedDateTime

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = HotelEntity::class,
            parentColumns = ["id"],
            childColumns = ["hotelId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FlightEntity::class,
            parentColumns = ["id"],
            childColumns = ["flightId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class ItineraryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val title: String,
    val description: String? = null,
    val placeId: String?,
    val viewType: String? = null,
    val startDateTime: ZonedDateTime? = null,
    val endDateTime: ZonedDateTime? = null,
    val category: ItineraryCategory = ItineraryCategory.OTHER,
    val status: ItineraryItemStatus = ItineraryItemStatus.PENDING,
    val hotelId: Long? = null,
    val flightId: Long? = null,
    val dayDate: LocalDate? = null,
    val orderIndex: Long = 0L
)

enum class ItineraryItemStatus {
    PENDING,
    COMPLETED,
    CANCELLED,
    NONE
}

enum class ItineraryCategory(val displayName: String) {
    FLIGHT("Flight"),
    HOTEL("Hotel"),
    ACTIVITY("Activity"),
    TRANSPORT("Transport"),
    MEAL("Meal"),
    OTHER("Other")
}