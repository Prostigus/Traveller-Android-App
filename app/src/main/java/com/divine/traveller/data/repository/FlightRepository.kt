package com.divine.traveller.data.repository

import com.divine.traveller.data.dao.FlightDao
import com.divine.traveller.data.entity.FlightEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlightRepository @Inject constructor(
    private val flightDao: FlightDao
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

    suspend fun update(flight: FlightEntity) {
        flightDao.update(flight)
    }

    suspend fun delete(flight: FlightEntity) {
        flightDao.delete(flight)
    }

    suspend fun getById(id: Long): FlightEntity? {
        return flightDao.getById(id)
    }

    fun getByTripId(tripId: Long) = flightDao.getByTripId(tripId)

    fun getAll() = flightDao.getAll()
}