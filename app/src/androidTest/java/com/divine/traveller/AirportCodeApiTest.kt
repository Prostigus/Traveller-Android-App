package com.divine.traveller

// AirportCodeApiTest.kt
import android.content.Context
import android.content.res.Resources
import com.divine.traveller.util.AirportCodeParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import java.io.ByteArrayInputStream

class AirportCodeApiTest {
    @Test
    fun testGetIataCode_returnsClosestAirport() = runBlocking {
        val csv = """
            "id","ident","type","name","latitude_deg","longitude_deg","elevation_ft","continent","iso_country","iso_region","municipality","scheduled_service","icao_code","iata_code","gps_code","local_code","home_link","wikipedia_link","keywords"
            1,"AAA","airport","Test Airport 1",10.0,20.0,100,"EU","FR","FR-01","City1","no","ICAO1","TST1","GPS1","LC1","","",""
            2,"BBB","airport","Test Airport 2",15.0,25.0,200,"EU","FR","FR-02","City2","no","ICAO2","TST2","GPS2","LC2","","",""
        """.trimIndent()

        val mockContext = Mockito.mock(Context::class.java)
        val mockResources = Mockito.mock(Resources::class.java)
        Mockito.`when`(mockContext.resources).thenReturn(mockResources)
        Mockito.`when`(mockResources.openRawResource(com.divine.traveller.R.raw.airports_v1))
            .thenReturn(ByteArrayInputStream(csv.toByteArray()))

        val api = AirportCodeParser(mockContext)
        val result = api.getIataCode(10.1, 20.1) // Closest to Test Airport 1

        assertEquals("TST1", result)
    }
}