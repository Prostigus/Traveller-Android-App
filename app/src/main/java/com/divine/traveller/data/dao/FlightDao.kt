package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.divine.traveller.data.entity.FlightEntity
import com.divine.traveller.data.entity.FlightWithAirports
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flight: FlightEntity): Long

    @Update
    suspend fun update(flight: FlightEntity)

    @Delete
    suspend fun delete(flight: FlightEntity)

    @Transaction
    @Query("SELECT * FROM flights WHERE id = :id")
    suspend fun getById(id: Long): FlightWithAirports?

    @Transaction
    @Query("SELECT * FROM flights WHERE tripId = :tripId ORDER BY departureDateTime ASC")
    fun getByTripId(tripId: Long): Flow<List<FlightWithAirports>>

    @Transaction
    @Query("SELECT * FROM flights")
    fun getAll(): Flow<List<FlightWithAirports>>
}