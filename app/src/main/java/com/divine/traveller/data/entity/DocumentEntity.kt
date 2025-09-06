package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val fileName: String,
    val fileUrl: String,
    val flightHotelId: Long? = null,
    val type: FlightHotelType? = null,
    val description: String? = null,
    val uploadedAt: Date = Date()
)

enum class FlightHotelType {
    FLIGHT,
    HOTEL
}