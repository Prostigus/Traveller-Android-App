package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.divine.traveller.data.entity.TripEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): TripEntity?

    @Query("SELECT * FROM trips WHERE isCompleted = 0 ORDER BY startDate ASC")
    fun getUpcomingTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isCompleted = 1 ORDER BY endDate DESC")
    fun getCompletedTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE startDate <= :today AND endDate >= :today")
    fun getCurrentTrips(today: Date = Date()): Flow<List<TripEntity>>

    @Insert
    suspend fun insertTrip(tripEntity: TripEntity): Long

    @Update
    suspend fun updateTrip(tripEntity: TripEntity)

    @Delete
    suspend fun deleteTrip(tripEntity: TripEntity)

    @Query("UPDATE trips SET isCompleted = :isCompleted, updatedAt = :updatedAt WHERE id = :tripId")
    suspend fun markTripCompleted(tripId: Long, isCompleted: Boolean = true, updatedAt: Date = Date())
}