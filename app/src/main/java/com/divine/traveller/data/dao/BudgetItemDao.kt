package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.divine.traveller.data.entity.BudgetItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budgetItem: BudgetItemEntity): Long

    @Update
    suspend fun update(budgetItem: BudgetItemEntity)

    @Delete
    suspend fun delete(budgetItem: BudgetItemEntity)

    @Query("SELECT * FROM budget_items WHERE id = :id")
    suspend fun getById(id: Long): BudgetItemEntity?

    @Query("SELECT * FROM budget_items WHERE tripId = :tripId")
    fun getByTripId(tripId: Long): Flow<List<BudgetItemEntity>>

    @Query("SELECT * FROM budget_items")
    fun getAll(): Flow<List<BudgetItemEntity>>
}