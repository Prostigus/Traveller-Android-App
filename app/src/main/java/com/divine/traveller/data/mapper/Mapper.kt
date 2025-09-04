package com.divine.traveller.data.mapper

import com.divine.traveller.data.entity.TripEntity
import com.divine.traveller.model.Trip

fun TripEntity.toDomainModel(): Trip {
    return Trip(
        id = id,
        name = name,
        description = description,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        budget = budget,
        currency = currency,
        imageUrl = imageUrl,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Trip.toEntity(): TripEntity {
    return TripEntity(
        id = id,
        name = name,
        description = description,
        destination = destination,
        startDate = startDate,
        endDate = endDate,
        budget = budget,
        currency = currency,
        imageUrl = imageUrl,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}