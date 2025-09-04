package com.divine.traveller.data.repository

import com.divine.traveller.data.dao.TripDao
import com.divine.traveller.data.entity.TripEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TripRepository @Inject constructor(
    private val tripDao: TripDao
) {
    fun getAllTrips(): Flow<List<TripEntity>> = tripDao.getAllTrips()

    suspend fun getTripById(tripId: Long): TripEntity? = tripDao.getTripById(tripId)

    fun getUpcomingTrips(): Flow<List<TripEntity>> = tripDao.getUpcomingTrips()

    fun getCompletedTrips(): Flow<List<TripEntity>> = tripDao.getCompletedTrips()

    fun getCurrentTrips(): Flow<List<TripEntity>> = tripDao.getCurrentTrips()

    suspend fun insertTrip(tripEntity: TripEntity): Long = tripDao.insertTrip(tripEntity)

    suspend fun updateTrip(tripEntity: TripEntity) = tripDao.updateTrip(tripEntity)

    suspend fun deleteTrip(tripEntity: TripEntity) = tripDao.deleteTrip(tripEntity)

    suspend fun markTripCompleted(tripId: Long, isCompleted: Boolean = true) {
        tripDao.markTripCompleted(tripId, isCompleted, Date())
    }
}