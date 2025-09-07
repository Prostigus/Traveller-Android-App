package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val startDateTime: Long,
    val endDateTime: Long,
    val category: ItineraryCategory = ItineraryCategory.OTHER,
    val status: ItineraryItemStatus = ItineraryItemStatus.PENDING,
    val hotelId: Long? = null,
    val flightId: Long? = null
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