package com.github.mantasjasikenas.core.domain.model.period

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class Period(
    val start: LocalDate = LocalDate(1, 1, 1),
    val end: LocalDate = LocalDate(1, 1, 1)
) {
    companion object;

    override fun toString(): String {
        return "$start - $end"
    }

    fun daysUntilEnd(timeZone: TimeZone = TimeZone.currentSystemDefault()): Int {
        return currentLocalDate(timeZone).daysUntil(end)
    }
}

fun LocalDate.isBetween(
    start: LocalDate, end: LocalDate
): Boolean {
    return this in start..end
}

fun LocalDate.isInPeriod(period: Period): Boolean {
    return this.isBetween(
        period.start, period.end
    )
}

fun Period.previousMonthlyDeprecated(
    minusMonths: Int = 1
): Period {
    val periodStart = start.minus(
        minusMonths, DateTimeUnit.MONTH
    )
    val periodEnd = end.minus(
        minusMonths, DateTimeUnit.MONTH
    )

    return Period(
        periodStart, periodEnd
    )
}

fun Period.contains(isoDate: String): Boolean {
    return LocalDateTime
        .parse(isoDate)
        .date
        .isBetween(start, end)
}

fun Period.Companion.getMonthlyPeriod(startDayInclusive: Int): Period {
    return getPeriod(
        startDayInclusive, startDayInclusive
    )
}

fun Period.Companion.getPeriod(
    startDayInclusive: Int, endDateExclusive: Int
): Period {
    val currentDate = currentLocalDate()

    val periodStart: LocalDate
    val periodEnd: LocalDate

    if (currentDate.dayOfMonth < startDayInclusive) {
        periodStart = LocalDate(currentDate.year, currentDate.monthNumber, startDayInclusive)
            .minus(1, DateTimeUnit.MONTH)
        periodEnd = LocalDate(currentDate.year, currentDate.monthNumber, endDateExclusive - 1)
    } else {
        periodStart = LocalDate(currentDate.year, currentDate.monthNumber, startDayInclusive)
        periodEnd = LocalDate(currentDate.year, currentDate.monthNumber, endDateExclusive - 1)
            .plus(1, DateTimeUnit.MONTH)
    }

    return Period(
        periodStart, periodEnd
    )
}

internal fun currentLocalDate(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDate {
    return currentLocalDateTime(timeZone = timeZone).date
}

internal fun currentLocalDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone = timeZone)
}