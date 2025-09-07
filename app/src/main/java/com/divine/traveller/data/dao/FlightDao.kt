package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.divine.traveller.data.entity.FlightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flight: FlightEntity): Long

    @Update
    suspend fun update(flight: FlightEntity)

    @Delete
    suspend fun delete(flight: FlightEntity)

    @Query("SELECT * FROM flights WHERE id = :id")
    suspend fun getById(id: Long): FlightEntity?

    @Query("SELECT * FROM flights WHERE tripId = :tripId")
    fun getByTripId(tripId: Long): Flow<List<FlightEntity>>

    @Query("SELECT * FROM flights")
    fun getAll(): Flow<List<FlightEntity>>
}