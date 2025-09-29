package com.divine.traveller.ui.itinerary

import androidx.compose.ui.graphics.Color
import com.divine.traveller.data.viewmodel.HotelsByDay
import java.time.LocalDate

data class DayHighlightInfo(
    val colors: List<Color> = emptyList(),
    val isGradient: Boolean = false,
    val leftContinues: Boolean = false,
    val rightContinues: Boolean = false
)

fun computeDayHighlightInfo(
    date: LocalDate,
    bookingsByDay: Map<LocalDate, HotelsByDay>,
    colorMap: Map<Long, Color>
): DayHighlightInfo {
    val todayBookings = bookingsByDay[date]?.hotels.orEmpty()
    if (todayBookings.isEmpty()) return DayHighlightInfo()

    if (todayBookings.size >= 2) {
        val distinct = todayBookings.distinctBy { it.id }
        if (distinct.size == 2) {
            val a = distinct[0]
            val b = distinct[1]
            val prevHasA = bookingsByDay[date.minusDays(1)]?.hotels.orEmpty().any { it.id == a.id }
            val nextHasB = bookingsByDay[date.plusDays(1)]?.hotels.orEmpty().any { it.id == b.id }
            return DayHighlightInfo(
                colors = listOfNotNull(colorMap[a.id], colorMap[b.id]),
                isGradient = true,
                leftContinues = prevHasA,
                rightContinues = nextHasB
            )
        }
    }

    val booking = todayBookings.first()
    val color = colorMap[booking.id] ?: return DayHighlightInfo()
    val prevHasSame = bookingsByDay[date.minusDays(1)]?.hotels.orEmpty().any { it.id == booking.id }
    val nextHasSame = bookingsByDay[date.plusDays(1)]?.hotels.orEmpty().any { it.id == booking.id }

    return DayHighlightInfo(
        colors = listOf(color),
        isGradient = false,
        leftContinues = prevHasSame,
        rightContinues = nextHasSame
    )
}