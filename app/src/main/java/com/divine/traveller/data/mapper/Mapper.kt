package com.divine.traveller.data.mapper

import com.divine.traveller.data.entity.BudgetItemEntity
import com.divine.traveller.data.entity.DocumentEntity
import com.divine.traveller.data.entity.FlightEntity
import com.divine.traveller.data.entity.HotelEntity
import com.divine.traveller.data.entity.ItineraryItemEntity
import com.divine.traveller.data.entity.TripEntity
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
    startDateUtcMillis = startDateUtcMillis,
    endDateUtcMillis = endDateUtcMillis,
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
    startDateUtcMillis = startDateUtcMillis,
    endDateUtcMillis = endDateUtcMillis,
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

fun FlightEntity.toDomainModel() = FlightModel(
    id,
    tripId,
    airline,
    flightNumber,
    departureAirport,
    arrivalAirport,
    departureDate,
    departureZoneId,
    arrivalDate,
    arrivalZoneId,
    status
)

fun HotelEntity.toDomainModel() = HotelModel(
    id, tripId, name, address, checkInDate, checkOutDate, bookingReference, placeId, status
)

fun ItineraryItemEntity.toDomainModel() = ItineraryItemModel(
    id,
    tripId,
    title,
    description,
    placeId,
    viewType,
    startDateTime,
    endDateTime,
    category,
    status,
    hotelId,
    flightId
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
    departureAirport = departureAirport,
    arrivalAirport = arrivalAirport,
    departureDate = departureDate,
    departureZoneId = departureZoneId,
    arrivalDate = arrivalDate,
    arrivalZoneId = arrivalZoneId,
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
    hotelId = hotelId,
    flightId = flightId
)