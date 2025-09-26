package com.divine.traveller.data.mapper

import com.divine.traveller.data.entity.AirportEntity
import com.divine.traveller.data.entity.BudgetItemEntity
import com.divine.traveller.data.entity.DocumentEntity
import com.divine.traveller.data.entity.FlightEntity
import com.divine.traveller.data.entity.FlightWithAirports
import com.divine.traveller.data.entity.HotelEntity
import com.divine.traveller.data.entity.ItineraryItemEntity
import com.divine.traveller.data.entity.ItineraryItemWithRelations
import com.divine.traveller.data.entity.TripEntity
import com.divine.traveller.data.model.AirportModel
import com.divine.traveller.data.model.BudgetItemModel
import com.divine.traveller.data.model.DocumentModel
import com.divine.traveller.data.model.FlightModel
import com.divine.traveller.data.model.HotelModel
import com.divine.traveller.data.model.ItineraryItemModel
import com.divine.traveller.data.model.TripModel

fun TripEntity.toDomainModel() = TripModel(
    id = id,
    name = name,
    description = description,
    destination = destination,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    budget = budget,
    currency = currency,
    imageUrl = imageUrl,
    isCompleted = isCompleted,
    createdAtUtcMillis = createdAtUtcMillis,
    updatedAtUtcMillis = updatedAtUtcMillis,
    destinationZoneIdString = destinationZoneIdString
)

fun TripModel.toEntity() = TripEntity(
    id = id,
    name = name,
    description = description,
    destination = destination,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    budget = budget,
    currency = currency,
    imageUrl = imageUrl,
    isCompleted = isCompleted,
    createdAtUtcMillis = createdAtUtcMillis,
    updatedAtUtcMillis = updatedAtUtcMillis,
    destinationZoneIdString = destinationZoneIdString
)

fun BudgetItemEntity.toDomainModel() = BudgetItemModel(
    id, tripId, category, amount, currency, notes, createdAt, payedBy
)

fun DocumentEntity.toDomainModel() = DocumentModel(
    id, tripId, fileName, fileUrl, flightHotelId, type, description, uploadedAt
)


fun HotelEntity.toDomainModel() = HotelModel(
    id, tripId, name, address, checkInDate, checkOutDate, bookingReference, placeId, status
)


fun ItineraryItemModel.toEntity() = ItineraryItemEntity(
    id = id,
    tripId = tripId,
    title = title,
    description = description,
    placeId = placeId,
    viewType = viewType,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    category = category,
    status = status,
    hotelId = hotel?.id,
    flightId = flight?.id,
    dayDate = dayDate,
    orderIndex = orderIndex
)

fun BudgetItemModel.toEntity() = BudgetItemEntity(
    id = id,
    tripId = tripId,
    category = category,
    amount = amount,
    currency = currency,
    notes = notes,
    createdAt = createdAt,
    payedBy = payedBy
)

fun DocumentModel.toEntity() = DocumentEntity(
    id = id,
    tripId = tripId,
    fileName = fileName,
    fileUrl = fileUrl,
    flightHotelId = flightHotelId,
    type = type,
    description = description,
    uploadedAt = uploadedAt
)

fun FlightModel.toEntity() = FlightEntity(
    id = id,
    tripId = tripId,
    airline = airline,
    flightNumber = flightNumber,
    departureAirportId = departureAirport?.id,
    arrivalAirportId = arrivalAirport?.id,
    departureDateTime = departureDateTime,
    arrivalDateTime = arrivalDateTime,
    status = status
)

fun HotelModel.toEntity() = HotelEntity(
    id = id,
    tripId = tripId,
    name = name,
    address = address,
    checkInDate = checkInDate,
    checkOutDate = checkOutDate,
    bookingReference = bookingReference,
    placeId = placeId,
    status = status
)

fun AirportEntity.toDomainModel() = AirportModel(
    id = id,
    ident = ident,
    type = type,
    name = name,
    latitudeDeg = latitudeDeg,
    longitudeDeg = longitudeDeg,
    elevationFt = elevationFt,
    continent = continent,
    isoCountry = isoCountry,
    isoRegion = isoRegion,
    municipality = municipality,
    scheduledService = scheduledService,
    icaoCode = icaoCode,
    iataCode = iataCode,
    gpsCode = gpsCode,
    localCode = localCode,
    homeLink = homeLink,
    wikipediaLink = wikipediaLink,
    keywords = keywords
)

fun AirportModel.toEntity() = AirportEntity(
    id = id,
    ident = ident,
    type = type,
    name = name,
    latitudeDeg = latitudeDeg,
    longitudeDeg = longitudeDeg,
    elevationFt = elevationFt,
    continent = continent,
    isoCountry = isoCountry,
    isoRegion = isoRegion,
    municipality = municipality,
    scheduledService = scheduledService,
    icaoCode = icaoCode,
    iataCode = iataCode,
    gpsCode = gpsCode,
    localCode = localCode,
    homeLink = homeLink,
    wikipediaLink = wikipediaLink,
    keywords = keywords
)

fun FlightWithAirports.toDomainModel(): FlightModel {
    return FlightModel(
        id = flight.id,
        tripId = flight.tripId,
        airline = flight.airline,
        flightNumber = flight.flightNumber,
        departureAirport = departureAirportEntity?.toDomainModel(),
        arrivalAirport = arrivalAirportEntity?.toDomainModel(),
        departureDateTime = flight.departureDateTime,
        arrivalDateTime = flight.arrivalDateTime,
        status = flight.status
    )
}

fun ItineraryItemWithRelations.toDomainModel(): ItineraryItemModel {
    return ItineraryItemModel(
        id = itineraryItem.id,
        tripId = itineraryItem.tripId,
        title = itineraryItem.title,
        description = itineraryItem.description,
        placeId = itineraryItem.placeId,
        viewType = itineraryItem.viewType,
        startDateTime = itineraryItem.startDateTime,
        endDateTime = itineraryItem.endDateTime,
        category = itineraryItem.category,
        status = itineraryItem.status,
        hotel = hotel?.toDomainModel(),
        flight = flight?.toDomainModel(),
        dayDate = itineraryItem.dayDate,
        orderIndex = itineraryItem.orderIndex
    )
}