package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.divine.traveller.data.entity.TripEntity
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime
import java.util.Date

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDateTime DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): TripEntity?

    @Query("SELECT * FROM trips WHERE isCompleted = 0 AND startDateTime > :today ORDER BY startDateTime ASC")
    fun getUpcomingTrips(today: ZonedDateTime = ZonedDateTime.now()): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE isCompleted = 1 OR endDateTime < :today ORDER BY endDateTime DESC")
    fun getCompletedTrips(today: ZonedDateTime = ZonedDateTime.now()): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE startDateTime <= :today AND endDateTime >= :today")
    fun getCurrentTrips(today: ZonedDateTime = ZonedDateTime.now()): Flow<List<TripEntity>>

    @Insert
    suspend fun insertTrip(tripEntity: TripEntity): Long

    @Update
    suspend fun updateTrip(tripEntity: TripEntity)

    @Delete
    suspend fun deleteTrip(tripEntity: TripEntity)

    @Query("UPDATE trips SET isCompleted = :isCompleted, updatedAtUtcMillis = :updatedAt WHERE id = :tripId")
    suspend fun markTripCompleted(
        tripId: Long,
        isCompleted: Boolean = true,
        updatedAt: Date = Date()
    )
}