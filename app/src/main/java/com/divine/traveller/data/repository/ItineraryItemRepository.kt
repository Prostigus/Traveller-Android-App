package com.divine.traveller.data.repository

import com.divine.traveller.data.dao.ItineraryItemDao
import com.divine.traveller.data.entity.ItineraryItemEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItineraryItemRepository @Inject constructor(
    private val itineraryItemDao: ItineraryItemDao
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

    suspend fun getById(id: Long): ItineraryItemEntity? {
        return itineraryItemDao.getById(id)
    }

    fun getByTripId(tripId: Long) = itineraryItemDao.getByTripId(tripId)

    fun getAll() = itineraryItemDao.getAll()
}