package com.divine.traveller.data.repository

import com.divine.traveller.data.dao.ItineraryItemDao
import com.divine.traveller.data.entity.ItineraryItemEntity
import com.divine.traveller.data.entity.ItineraryItemWithRelations
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryItemRepository @Inject constructor(
    private val itineraryItemDao: ItineraryItemDao,
) {

    suspend fun insert(item: ItineraryItemEntity): Long {
        return itineraryItemDao.insert(item)
    }

    suspend fun update(item: ItineraryItemEntity) {
        itineraryItemDao.update(item)
    }

    suspend fun delete(item: ItineraryItemEntity) {
        itineraryItemDao.delete(item)
    }

    suspend fun getById(id: Long): ItineraryItemWithRelations? {
        return itineraryItemDao.getById(id)
    }

    fun getByTripId(tripId: Long) = itineraryItemDao.getByTripId(tripId)

    fun getAll() = itineraryItemDao.getAll()

    suspend fun getItineraryItemsForFlight(flightId: Long) =
        itineraryItemDao.getItineraryItemsForFlight(flightId)

    fun getItemsForDayOrdered(tripId: Long, dayDate: LocalDate) =
        itineraryItemDao.getItemsForDayOrdered(tripId, dayDate)

    suspend fun getMaxOrderIndexForDay(tripId: Long, dayDate: LocalDate): Long? {
        return itineraryItemDao.getMaxOrderIndexForDay(tripId, dayDate)
    }

    suspend fun insertWithNextOrder(
        item: ItineraryItemEntity,
        day: LocalDate? = item.dayDate
    ): Long {
        return itineraryItemDao.insertWithNextOrder(item, day)
    }

    suspend fun reorderItemsForDay(tripId: Long, dayDate: LocalDate, orderedIds: List<Long>) {
        return itineraryItemDao.reorderItemsForDay(tripId, dayDate, orderedIds)
    }

}