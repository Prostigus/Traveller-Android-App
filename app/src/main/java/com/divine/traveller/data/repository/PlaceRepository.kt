package com.divine.traveller.data.repository

import com.divine.traveller.data.dao.PlaceDao
import com.divine.traveller.data.entity.PlaceEntity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepository @Inject constructor(
    private val placeDao: PlaceDao,
    private val placesClient: PlacesClient
) {
    private val placeFields = listOf(
        Place.Field.ID,
        Place.Field.DISPLAY_NAME,
        Place.Field.FORMATTED_ADDRESS,
        Place.Field.LOCATION,
        Place.Field.GOOGLE_MAPS_URI,
        Place.Field.PRIMARY_TYPE
    )

    suspend fun getPlace(id: String): Place {
        // return cached if present (mapped to SDK Place)
        placeDao.getById(id)?.let { return entityToPlace(it) }

        // otherwise fetch from network, cache and return
        val request = FetchPlaceRequest.newInstance(id, placeFields)

        val response = try {
            withContext(Dispatchers.IO) {
                Tasks.await(placesClient.fetchPlace(request))
            }
        } catch (e: Exception) {
            // network / places error -> return null
            return Place.builder().setId(id).build()
        }

        val place = response.place

        // cache the fetched Place
        val entity = PlaceEntity(
            id = place.id ?: id,
            displayName = place.displayName,
            address = place.formattedAddress,
            latitude = place.location?.latitude,
            longitude = place.location?.longitude,
            googleMapsUri = place.googleMapsUri?.toString(),
            primaryType = place.primaryType,
            updatedAt = System.currentTimeMillis()
        )

        placeDao.upsert(entity)
        return place
    }

    suspend fun cachePlace(place: Place) {
        val id = place.id ?: return
        val entity = PlaceEntity(
            id = place.id ?: id,
            displayName = place.displayName,
            address = place.formattedAddress,
            latitude = place.location?.latitude,
            longitude = place.location?.longitude,
            googleMapsUri = place.googleMapsUri?.toString(),
            primaryType = place.primaryType,
            updatedAt = System.currentTimeMillis()
        )
        placeDao.upsert(entity)
    }

    private fun entityToPlace(entity: PlaceEntity): Place {
        val builder = Place.builder()
            .setId(entity.id)
            .setFormattedAddress(entity.address)
            .setDisplayName(entity.displayName)

        if (entity.latitude != null && entity.longitude != null) {
            builder.setLocation(LatLng(entity.latitude, entity.longitude))
        }

        return builder.build()
    }

    suspend fun awaitFindAutocompletePredictions(
        value: String,
        includedTypes: List<String> = emptyList()
    ): List<AutocompletePrediction>? =
        withContext(Dispatchers.IO) {
            try {
                val response = placesClient.awaitFindAutocompletePredictions {
                    query = value
                    if (includedTypes.isNotEmpty()) {
                        typesFilter = includedTypes
                    }
                }
                response.autocompletePredictions
            } catch (e: Exception) {
                null
            }
        }

    suspend fun searchCached(query: String) = placeDao.searchByName("%$query%")
}