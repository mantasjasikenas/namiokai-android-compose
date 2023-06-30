package com.github.mantasjasikenas.namiokai.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class Period(
    val start: LocalDate,
    val end: LocalDate
) {
    companion object;

    override fun toString(): String {
        return "$start - $end"
    }
}

fun LocalDate.isBetween(start: LocalDate, end: LocalDate): Boolean {
    return this in start..end
}

fun LocalDate.isInPeriod(period: Period): Boolean {
    return this.isBetween(period.start, period.end)
}

fun Period.Companion.getMonthlyPeriod(startDayInclusive: Int): Period {
    return getPeriod(startDayInclusive, startDayInclusive)
}

fun Period.Companion.getPeriod(startDayInclusive: Int, endDateExclusive: Int): Period {
    val periodStart: LocalDate
    val periodEnd: LocalDate

    val currentDate = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()
    ).date

    if (currentDate.dayOfMonth < startDayInclusive) {
        periodStart = LocalDate(currentDate.year, currentDate.monthNumber - 1, startDayInclusive)
        periodEnd = LocalDate(currentDate.year, currentDate.monthNumber, endDateExclusive - 1)
    } else {
        periodStart = LocalDate(currentDate.year, currentDate.monthNumber, startDayInclusive)
        periodEnd = LocalDate(currentDate.year, currentDate.monthNumber + 1, endDateExclusive - 1)
    }

    return Period(periodStart, periodEnd)
}