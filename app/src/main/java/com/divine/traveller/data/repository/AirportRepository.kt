package com.divine.traveller.data.repository

import android.content.Context
import android.util.Log
import com.divine.traveller.data.dao.AirportDao
import com.divine.traveller.data.entity.AirportEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Singleton
class AirportRepository @Inject constructor(
    private val airportDao: AirportDao,
    @ApplicationContext private val context: Context
) {
    private val prefsName = "airport_prefs"
    private val prefsKeyVersion = "airports_loaded_version"

    suspend fun loadFromCsvIfNeeded(resourceId: Int, versionName: String) =
        withContext(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                val loadedVersion = prefs.getString(prefsKeyVersion, null)
                val dbCount = airportDao.count()
                if (loadedVersion == versionName && dbCount > 0) return@withContext

                val inputStream = context.resources.openRawResource(resourceId)
                val reader = BufferedReader(inputStream.reader())
                val list = ArrayList<AirportEntity>()
                reader.readLine() // skip header
                reader.forEachLine { line ->
                    val cols = line.split(",")
                    if (cols.size > 17) {
                        val id = cols[0].toLongOrNull() ?: return@forEachLine
                        val ident = cols[1].removeSurrounding("\"")
                        val type = cols[2].removeSurrounding("\"")
                        val name = cols[3].removeSurrounding("\"")
                        val lat = cols[4].toDoubleOrNull()
                        val lon = cols[5].toDoubleOrNull()
                        val elevation = cols[6].toIntOrNull()
                        val continent = cols[7].removeSurrounding("\"")
                        val isoCountry = cols[8].removeSurrounding("\"")
                        val isoRegion = cols[9].removeSurrounding("\"")
                        val municipality = cols[10].removeSurrounding("\"")
                        val scheduledService = cols[11].removeSurrounding("\"")
                        val icao = cols[12].removeSurrounding("\"")
                        val iata = cols[13].removeSurrounding("\"")
                        val gps = cols[14].removeSurrounding("\"")
                        val local = cols[15].removeSurrounding("\"")
                        val home = cols[16].removeSurrounding("\"")
                        val wiki = cols[17].removeSurrounding("\"")
                        val keywords =
                            if (cols.size > 18) cols[18].removeSurrounding("\"") else null

                        val airport = AirportEntity(
                            id = id,
                            ident = ident,
                            type = type,
                            name = name,
                            latitudeDeg = lat,
                            longitudeDeg = lon,
                            elevationFt = elevation,
                            continent = continent,
                            isoCountry = isoCountry,
                            isoRegion = isoRegion,
                            municipality = municipality,
                            scheduledService = scheduledService,
                            icaoCode = icao,
                            iataCode = iata,
                            gpsCode = gps,
                            localCode = local,
                            homeLink = home,
                            wikipediaLink = wiki,
                            keywords = keywords
                        )
                        list.add(airport)
                    }
                }
                reader.close()

                // Insert in chunks to avoid huge single transaction
                val chunkSize = 1000
                var i = 0
                while (i < list.size) {
                    val end = min(i + chunkSize, list.size)
                    airportDao.insertAll(list.subList(i, end))
                    i = end
                }

                prefs.edit().putString(prefsKeyVersion, versionName).apply()
                Log.i("AirportRepo", "Loaded ${list.size} airports into DB (version=$versionName)")
            } catch (e: Exception) {
                Log.e("AirportRepo", "Failed to load airports", e)
            }
        }

    // Find nearest iata by searching a bounding box first then computing exact distance in Kotlin
    suspend fun findNearestIata(
        lat: Double,
        lng: Double,
        searchKm: Double = 100.0
    ): AirportEntity? = withContext(Dispatchers.IO) {
        // approximate degrees for the given km radius (1 deg lat ~ 111 km)
        val deg = searchKm / 111.0
        val candidates = airportDao.findInBox(lat - deg, lat + deg, lng - deg, lng + deg)
        if (candidates.isEmpty()) {
            // fallback to a wider box
            val wide =
                airportDao.findInBox(lat - deg * 5, lat + deg * 5, lng - deg * 5, lng + deg * 5)
            return@withContext nearestFromList(lat, lng, wide)
        }
        return@withContext nearestFromList(lat, lng, candidates)
    }

    private fun nearestFromList(
        lat: Double,
        lng: Double,
        list: List<AirportEntity>
    ): AirportEntity? {
        var best: AirportEntity? = null
        var bestDist = Double.MAX_VALUE
        for (a in list) {
            val la = a.latitudeDeg ?: continue
            val lo = a.longitudeDeg ?: continue
            val d = haversine(lat, lng, la, lo)
            if (d < bestDist) {
                bestDist = d
                best = a
            }
        }
        return best
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
            sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(
                dLon / 2
            ).pow(2.0)
        return R * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}