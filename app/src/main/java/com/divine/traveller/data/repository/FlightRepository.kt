package com.divine.traveller.data.repository

import com.divine.traveller.data.dao.FlightDao
import com.divine.traveller.data.dao.ItineraryItemDao
import com.divine.traveller.data.entity.FlightEntity
import com.divine.traveller.data.entity.FlightWithAirports
import com.divine.traveller.data.entity.ItineraryCategory
import com.divine.traveller.data.entity.ItineraryItemEntity
import com.divine.traveller.data.entity.ItineraryItemStatus
import com.divine.traveller.data.mapper.toEntity
import com.divine.traveller.data.model.FlightModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(
    private val flightDao: FlightDao,
    private val itineraryItemDao: ItineraryItemDao
) {

    suspend fun insert(flight: FlightEntity): Long {
        // New entity (id == 0) -> normal insert
        if (flight.id == 0L) {
            return flightDao.insert(flight)
        }

        // Existing id: try insert (IGNORE). If conflict, update and return existing id.
        val insertedId = flightDao.insert(flight)
        return if (insertedId == -1L) {
            flightDao.update(flight)
            flight.id
        } else {
            insertedId
        }
    }

    suspend fun insertFlightWithItinerary(flight: FlightModel, onComplete: (Long) -> Unit = {}) {
        val id = insert(flight.toEntity())
        val itemsForFlight = itineraryItemDao.getItineraryItemsForFlight(id)

        val depIata = flight.departureAirport?.iataCode.orEmpty()
        val arrIata = flight.arrivalAirport?.iataCode.orEmpty()
        val depDisplay = depIata.ifEmpty { flight.departureAirport?.name ?: "Unknown" }
        val arrDisplay = arrIata.ifEmpty { flight.arrivalAirport?.name ?: "Unknown" }

        val title = "Flight from $depDisplay to $arrDisplay"
        val start = flight.departureDateTime
        val end = flight.arrivalDateTime
        val dayDate = flight.departureDateTime.toLocalDate()

        val existingItineraryEntity = itemsForFlight.firstOrNull()?.itineraryItem

        if (existingItineraryEntity != null) {
            // reuse existing row id and ordering, update its fields and attach flight id
            val updated = existingItineraryEntity.copy(
                title = title,
                tripId = flight.tripId,
                startDateTime = start,
                endDateTime = end,
                dayDate = dayDate,
                flightId = id
            )
            itineraryItemDao.update(updated)
        } else {
            // create new itinerary row and attach flight id
            val newItineraryEntity = ItineraryItemEntity(
                id = 0L,
                tripId = flight.tripId,
                title = title,
                description = null,
                placeId = null,
                viewType = null,
                startDateTime = start,
                endDateTime = end,
                category = ItineraryCategory.FLIGHT,
                status = ItineraryItemStatus.NONE,
                hotelId = null,
                flightId = id,
                dayDate = dayDate,
                orderIndex = 0L
            )
            itineraryItemDao.insertWithNextOrder(newItineraryEntity, dayDate)
        }

        onComplete(id)
    }

    suspend fun update(flight: FlightEntity) {
        flightDao.update(flight)
    }

    suspend fun delete(flight: FlightEntity) {
        flightDao.delete(flight)
    }

    suspend fun getById(id: Long): FlightWithAirports? {
        return flightDao.getById(id)
    }

    fun getByTripId(tripId: Long) = flightDao.getByTripId(tripId)

    fun getAll() = flightDao.getAll()
}