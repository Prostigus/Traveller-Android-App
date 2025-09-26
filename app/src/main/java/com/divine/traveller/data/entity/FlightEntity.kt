package com.divine.traveller.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.ZonedDateTime

@Entity(
    tableName = "flights",
    foreignKeys = [
        ForeignKey(
            entity = AirportEntity::class,
            parentColumns = ["id"],
            childColumns = ["departureAirportId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = AirportEntity::class,
            parentColumns = ["id"],
            childColumns = ["arrivalAirportId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("departureAirportId"), Index("arrivalAirportId")]
)
data class FlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val airline: String,
    val flightNumber: String,
    val departureAirportId: Long? = null,
    val arrivalAirportId: Long? = null,
    val departureDateTime: ZonedDateTime,
    val arrivalDateTime: ZonedDateTime,
    val status: FlightStatus
)

data class FlightWithAirports(
    @Embedded val flight: FlightEntity,
    @Relation(parentColumn = "departureAirportId", entityColumn = "id")
    val departureAirportEntity: AirportEntity? = null,
    @Relation(parentColumn = "arrivalAirportId", entityColumn = "id")
    val arrivalAirportEntity: AirportEntity? = null
)

enum class FlightStatus {
    SCHEDULED,
    ON_TIME,
    DELAYED,
    CANCELLED,
    LANDED
}