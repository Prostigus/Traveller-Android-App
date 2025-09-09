package com.divine.traveller.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.divine.traveller.data.entity.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ItineraryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItineraryItemEntity): Long

    @Update
    suspend fun update(item: ItineraryItemEntity)

    @Delete
    suspend fun delete(item: ItineraryItemEntity)

    @Transaction
    suspend fun deleteAndReorder(item: ItineraryItemEntity) {
        // remove item first
        delete(item)

        // if item had no day grouping, nothing to compact here
        val day = item.dayDate ?: return

        // fetch remaining items for that day and reindex with uniform gaps
        val remaining = getItemsForDayOrderedSuspend(item.tripId, day)
        var current = INITIAL_ORDER
        for (it in remaining) {
            updateOrderIndex(it.id, current)
            current += ORDER_GAP
        }
    }

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

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId AND dayDate = :dayDate ORDER BY orderIndex ASC")
    fun getItemsForDayOrdered(tripId: Long, dayDate: LocalDate): Flow<List<ItineraryItemEntity>>

    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId AND dayDate = :dayDate ORDER BY orderIndex ASC")
    suspend fun getItemsForDayOrderedSuspend(
        tripId: Long,
        dayDate: LocalDate
    ): List<ItineraryItemEntity>

    @Query("UPDATE itinerary_items SET orderIndex = :orderIndex WHERE id = :id")
    suspend fun updateOrderIndex(id: Long, orderIndex: Long)

    @Query("SELECT MAX(orderIndex) FROM itinerary_items WHERE tripId = :tripId AND dayDate = :dayDate")
    suspend fun getMaxOrderIndexForDay(tripId: Long, dayDate: LocalDate): Long?

    /**
     * Insert item with the next orderIndex for its trip/day.
     * If item.dayDate is null, fallback to a plain insert (caller can set orderIndex manually).
     */
    @Transaction
    suspend fun insertWithNextOrder(
        item: ItineraryItemEntity,
        day: LocalDate? = item.dayDate
    ): Long {
        val day = day ?: return insert(item)
        val maxOrder = getMaxOrderIndexForDay(item.tripId, day)
        val nextOrder = if (maxOrder == null) INITIAL_ORDER else maxOrder + ORDER_GAP
        val itemWithOrder = item.copy(orderIndex = nextOrder)
        return insert(itemWithOrder)
    }

    companion object {
        private const val ORDER_GAP = 1000L
        private const val INITIAL_ORDER = ORDER_GAP
    }

    @Transaction
    suspend fun reorderItemsForDay(tripId: Long, dayDate: LocalDate, orderedIds: List<Long>) {
        // fetch current items for the day
        val currentItems = getItemsForDayOrderedSuspend(tripId, dayDate)
        val validIds = currentItems.map { it.id }.toSet()

        // keep only ids that exist for that day, in the requested order
        val filtered = orderedIds.filter { it in validIds }

        // assign uniform gaps (INITIAL_ORDER, INITIAL_ORDER + ORDER_GAP, ...)
        var current = INITIAL_ORDER
        for (id in filtered) {
            updateOrderIndex(id, current)
            current += ORDER_GAP
        }
    }
}