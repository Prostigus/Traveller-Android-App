package com.divine.traveller.data.repository

import com.divine.traveller.data.dao.HotelDao
import com.divine.traveller.data.dao.ItineraryItemDao
import com.divine.traveller.data.entity.HotelEntity
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemEntity
import com.divine.traveller.data.entity.ItineraryItemStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelRepository @Inject constructor(
    private val hotelDao: HotelDao,
    private val itineraryItemDao: ItineraryItemDao
) {
    suspend fun insert(hotel: HotelEntity): Long {
        return if (hotel.id == 0L) {
            hotelDao.insert(hotel)
        } else {
            hotelDao.update(hotel)
            hotel.id
        }
    }

    suspend fun upsertHotelWithItinerary(hotel: HotelEntity, onComplete: (Long) -> Unit = {}) {
        val id = insert(hotel)
        val itemForHotel =
            itineraryItemDao.getItineraryItemForHotel(id).firstOrNull()?.itineraryItem

        val title = "Stay at: ${hotel.name}"
        val start = hotel.checkInDate
        val end = hotel.checkOutDate
        val dayDate = start.toLocalDate()

        if (itemForHotel == null) {
            val newItineraryItem = ItineraryItemEntity(
                id = 0L,
                tripId = hotel.tripId,
                title = title,
                description = null,
                placeId = hotel.placeId,
                viewType = null,
                startDateTime = start,
                endDateTime = end,
                category = ItineraryCategory.HOTEL,
                status = ItineraryItemStatus.NONE,
                hotelId = id,
                flightId = null,
                dayDate = dayDate,
                orderIndex = 0L
            )
            itineraryItemDao.insertWithNextOrder(newItineraryItem)
        } else {
            val updatedItineraryItem = itemForHotel.copy(
                title = hotel.name,
                startDateTime = start,
                endDateTime = end,
                dayDate = dayDate,
            )
            itineraryItemDao.update(updatedItineraryItem)
        }
        onComplete(id)
    }

    suspend fun delete(hotel: HotelEntity) {
        hotelDao.delete(hotel)
    }

    fun getByTripId(tripId: Long) = hotelDao.getByTripId(tripId)

    suspend fun getById(id: Long) = hotelDao.getById(id)

}