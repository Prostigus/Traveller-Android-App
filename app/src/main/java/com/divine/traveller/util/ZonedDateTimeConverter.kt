package com.divine.traveller.util

import androidx.room.TypeConverter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeConverter {
    @TypeConverter
    fun toZonedDateTime(value: String?): ZonedDateTime? {
        return value?.let {
            ZonedDateTime.parse(it, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }
    }

    @TypeConverter
    fun fromZonedDateTime(date: ZonedDateTime?): String? {
        return date?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }
}