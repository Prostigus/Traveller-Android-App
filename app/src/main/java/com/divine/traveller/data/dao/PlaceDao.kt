package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.divine.traveller.data.entity.PlaceEntity

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): PlaceEntity?

    @Query("SELECT * FROM places WHERE displayName LIKE :query ORDER BY updatedAt DESC")
    suspend fun searchByName(query: String): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(place: PlaceEntity)

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deleteById(id: String)
}