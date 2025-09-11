package com.divine.traveller.util

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import net.iakovlev.timeshape.TimeZoneEngine
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Converts milliseconds since epoch (UTC) to a Date object representing the local date (midnight).
 */
fun millisToLocalDate(millis: Long): Date {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = millis

    val localCalendar = Calendar.getInstance()
    localCalendar.set(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        0, 0, 0
    )
    localCalendar.set(Calendar.MILLISECOND, 0)

    return localCalendar.time
}

/**
 * Converts milliseconds since epoch (UTC) to ZonedDateTime in the given time zone.
 */
fun millisToZonedDateTime(millis: Long, zoneId: ZoneId): ZonedDateTime {
    return Instant.ofEpochMilli(millis).atZone(zoneId)
}

/**
 * Converts a Date object to UTC millis.
 */
fun dateToUtcMillis(date: Date): Long {
    return date.toInstant().toEpochMilli()
}

/**
 * Converts a ZonedDateTime to UTC millis.
 */
fun zonedDateTimeToUtcMillis(zdt: ZonedDateTime): Long {
    return zdt.toInstant().toEpochMilli()
}

/**
 * Gets the ZoneId from a time zone string (e.g., "Europe/Paris").
 */
fun zoneIdFromString(timeZoneId: String): ZoneId {
    return ZoneId.of(timeZoneId)
}

/**
 * Converts UTC millis to LocalDate in the given time zone.
 */
fun millisToLocalDateInZone(millis: Long, zoneId: ZoneId): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
}

fun millisToLocalDateTimeInZone(millis: Long, zoneId: ZoneId): LocalDateTime {
    return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDateTime()
}

fun correctUtcTimeStampForZonedDate(
    originalUtcMillis: Long,
    zoneId: ZoneId,
    atEndOfDay: Boolean = false
): ZonedDateTime {
    val selectedLocalDate =
        Instant.ofEpochMilli(originalUtcMillis).atZone(ZoneId.of("UTC")).toLocalDate()
    val zonedDateTime = if (atEndOfDay) {
        selectedLocalDate.atTime(23, 59, 59, 999_000_000).atZone(zoneId)
    } else {
        selectedLocalDate.atStartOfDay(zoneId)
    }
    return zonedDateTime
}

@TypeConverter
fun fromZoneId(zoneId: ZoneId): String = zoneId.id

@TypeConverter
fun toZoneId(zoneIdString: String): ZoneId = ZoneId.of(zoneIdString)

fun getZonedDateTimeForLocalDateTimeUsingTimeZoneEngine(
    localDateTime: LocalDateTime,
    latLng: LatLng,
    timeZoneEngine: TimeZoneEngine
): ZonedDateTime {
    val zoneId = timeZoneEngine.query(latLng.latitude, latLng.longitude)
        ?.orElse(ZoneId.of("UTC"))

    return localDateTime.atZone(zoneId)
}

fun formatZonedDateTime(zdt: ZonedDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm, MMM d, yyyy", Locale.ENGLISH)
    return zdt.format(formatter)
}
