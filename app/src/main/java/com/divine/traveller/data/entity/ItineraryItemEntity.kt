package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = HotelEntity::class,
            parentColumns = ["id"],
            childColumns = ["hotelId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = FlightEntity::class,
            parentColumns = ["id"],
            childColumns = ["flightId"],
            onDelete = ForeignKey.SET_NULL
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
    val startDateTime: Date,
    val endDateTime: Date,
    val category: ItineraryCategory = ItineraryCategory.OTHER,
    val status: ItineraryItemStatus = ItineraryItemStatus.PENDING,
    val hotelId: Long? = null,
    val flightId: Long? = null
)

enum class ItineraryItemStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}

enum class ItineraryCategory(val displayName: String) {
    FLIGHT("Flight"),
    HOTEL("Hotel"),
    ACTIVITY("Activity"),
    TRANSPORT("Transport"),
    MEAL("Meal"),
    OTHER("Other")
}