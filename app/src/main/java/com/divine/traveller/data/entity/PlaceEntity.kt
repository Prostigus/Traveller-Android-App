package com.divine.traveller.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: String,
    val displayName: String? = null,
    val address: String? = null,
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val iconUrl: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)