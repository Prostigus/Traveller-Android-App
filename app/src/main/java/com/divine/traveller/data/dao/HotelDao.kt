package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.divine.traveller.data.entity.HotelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(hotel: HotelEntity): Long

    @Update
    suspend fun update(hotel: HotelEntity)

    @Delete
    suspend fun delete(hotel: HotelEntity)

    @Query("SELECT * FROM hotels WHERE id = :id")
    suspend fun getById(id: Long): HotelEntity?

    @Query("SELECT * FROM hotels WHERE tripId = :tripId")
    fun getByTripId(tripId: Long): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels")
    fun getAll(): Flow<List<HotelEntity>>
}