package com.divine.traveller.data.model

data class AirportModel(
    val id: Long,
    val ident: String?,
    val type: String?,
    val name: String?,
    val latitudeDeg: Double?,
    val longitudeDeg: Double?,
    val elevationFt: Int?,
    val continent: String?,
    val isoCountry: String?,
    val isoRegion: String?,
    val municipality: String?,
    val scheduledService: String?,
    val icaoCode: String?,
    val iataCode: String?,
    val gpsCode: String?,
    val localCode: String?,
    val homeLink: String?,
    val wikipediaLink: String?,
    val keywords: String?
)