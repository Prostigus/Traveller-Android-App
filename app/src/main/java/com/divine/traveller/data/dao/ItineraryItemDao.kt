package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.divine.traveller.data.entity.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItineraryItemEntity): Long

    @Update
    suspend fun update(item: ItineraryItemEntity)

    @Delete
    suspend fun delete(item: ItineraryItemEntity)

    @Query("SELECT * FROM itinerary_items WHERE id = :id")
    suspend fun getById(id: Long): ItineraryItemEntity?

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId")
    fun getByTripId(tripId: Long): Flow<List<ItineraryItemEntity>>

    @Query("SELECT * FROM itinerary_items")
    fun getAll(): Flow<List<ItineraryItemEntity>>

    @Query("SELECT * FROM itinerary_items WHERE flightId = :flightId")
    suspend fun getItineraryItemsForFlight(flightId: Long): List<ItineraryItemEntity>

    @Query("SELECT * FROM itinerary_items")
    suspend fun getAllsuspend(): List<ItineraryItemEntity>
}