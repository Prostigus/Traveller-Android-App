package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.divine.traveller.data.entity.AirportEntity

@Dao
interface AirportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(airports: List<AirportEntity>)

    @Query("SELECT COUNT(*) FROM airports")
    suspend fun count(): Int

    @Query("SELECT * FROM airports WHERE latitudeDeg BETWEEN :minLat AND :maxLat AND longitudeDeg BETWEEN :minLng AND :maxLng")
    suspend fun findInBox(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<AirportEntity>

    @Query("SELECT * FROM airports WHERE iataCode = :iata LIMIT 1")
    suspend fun findByIata(iata: String): AirportEntity?
}