package com.divine.traveller.util

import android.content.Context
import android.util.Log
import com.divine.traveller.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class AirportCodeParser @Inject constructor(@ApplicationContext private val context: Context) {
    suspend fun getIataCode(lat: Double, lng: Double): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.airports)
            val reader = BufferedReader(inputStream.reader())
            reader.readLine() // skip header

            var closestIata: String? = null
            var minDistance = Double.MAX_VALUE

            reader.forEachLine { line ->
                val columns = line.split(",")
                if (columns.size > 14) {
                    val airportLat = columns[4].toDoubleOrNull()
                    val airportLng = columns[5].toDoubleOrNull()
                    val iataCode = columns[13].trim('"')
                    if (airportLat != null && airportLng != null && iataCode.isNotEmpty()) {
                        val distance = haversine(lat, lng, airportLat, airportLng)
                        if (distance < minDistance) {
                            minDistance = distance
                            closestIata = iataCode
                        }
                    }
                }
            }
            reader.close()
            closestIata
        } catch (e: Exception) {
            Log.e("AirportCodeApi", "Error reading airports.csv", e)
            null
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(
            dLon / 2
        ).pow(2)
        return R * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}