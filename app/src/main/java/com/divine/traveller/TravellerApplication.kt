package com.divine.traveller

import android.app.Application
import com.divine.traveller.data.repository.AirportRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TravellerApplication : Application() {
    @Inject
    lateinit var airportRepository: AirportRepository

    override fun onCreate() {
        super.onCreate()
        // load airports in background on app start; resource name/version should match your raw filename
        CoroutineScope(Dispatchers.IO).launch {
            airportRepository.loadFromCsvIfNeeded(R.raw.airports_v1, "airports_v1")
        }
    }
}