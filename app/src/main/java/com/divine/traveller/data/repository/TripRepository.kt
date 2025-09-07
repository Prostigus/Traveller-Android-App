package com.divine.traveller.data.repository

import com.divine.traveller.data.dao.TripDao
import com.divine.traveller.data.entity.TripEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TripRepository @Inject constructor(
    private val tripDao: TripDao
) {
    private val _currentTrip = MutableStateFlow<TripEntity?>(null)
    val currentTrip: StateFlow<TripEntity?> = _currentTrip.asStateFlow()

    /**
     * Return cached trip if it matches `tripId`, otherwise fetch from DB and update cache.
     */
    suspend fun getTripByIdCached(tripId: Long): TripEntity? {
        val cached = _currentTrip.value
        if (cached?.id == tripId) return cached

        val fresh = tripDao.getTripById(tripId)
        _currentTrip.value = fresh
        return fresh
    }

    /**
     * Force reload from DB and update cache.
     */
    suspend fun refreshTrip(tripId: Long): TripEntity? {
        val fresh = tripDao.getTripById(tripId)
        _currentTrip.value = fresh
        return fresh
    }

    /**
     * Manually set current trip (useful after creating/updating locally).
     */
    fun setCurrentTrip(trip: TripEntity?) {
        _currentTrip.value = trip
    }

    /**
     * Clear in-memory cache.
     */
    fun clearCache() {
        _currentTrip.value = null
    }
    fun getAllTrips(): Flow<List<TripEntity>> = tripDao.getAllTrips()

    suspend fun getTripById(tripId: Long): TripEntity? = tripDao.getTripById(tripId)

    fun getUpcomingTrips(): Flow<List<TripEntity>> = tripDao.getUpcomingTrips()

    fun getCompletedTrips(): Flow<List<TripEntity>> = tripDao.getCompletedTrips()

    fun getCurrentTrips(): Flow<List<TripEntity>> = tripDao.getCurrentTrips()

    suspend fun insertTrip(tripEntity: TripEntity): Long = tripDao.insertTrip(tripEntity)

    suspend fun updateTrip(tripEntity: TripEntity) {
        tripDao.updateTrip(tripEntity)
        // if cached trip is the same, update cache
        if (_currentTrip.value?.id == tripEntity.id) {
            _currentTrip.value = tripEntity
        }
    }

    suspend fun deleteTrip(tripEntity: TripEntity) {
        tripDao.deleteTrip(tripEntity)
        if (_currentTrip.value?.id == tripEntity.id) {
            _currentTrip.value = null
        }
    }

    suspend fun markTripCompleted(tripId: Long, isCompleted: Boolean = true) {
        tripDao.markTripCompleted(tripId, isCompleted, Date())
        // optionally refresh cache if it matches
        if (_currentTrip.value?.id == tripId) {
            // best-effort refresh
            _currentTrip.value = tripDao.getTripById(tripId)
        }
    }
}