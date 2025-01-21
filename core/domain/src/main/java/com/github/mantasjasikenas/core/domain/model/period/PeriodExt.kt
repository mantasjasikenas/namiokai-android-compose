package com.github.mantasjasikenas.core.domain.model.period

import com.github.mantasjasikenas.core.common.util.applyIf
import com.github.mantasjasikenas.core.common.util.currentLocalDate
import com.github.mantasjasikenas.core.domain.model.RecurrenceUnit
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

/**
 * Generates the current period based on the recurrence unit and start value.
 * The current period is generated based on the current date. The end date is inclusive by default.
 */
fun generateCurrentPeriod(
    recurrenceStartValue: Int,
    recurrenceUnit: RecurrenceUnit,
    endDateInclusive: Boolean = true,
    currentLocalDate: LocalDate = currentLocalDate()
): Period {
    return when (recurrenceUnit) {
        RecurrenceUnit.DAILY -> generateDailyPeriod(
            currentDate = currentLocalDate, endDateInclusive = endDateInclusive
        )

        RecurrenceUnit.WEEKLY -> generateWeeklyPeriod(
            startDayOfWeek = recurrenceStartValue,
            currentDate = currentLocalDate,
            endDateInclusive = endDateInclusive
        )

        RecurrenceUnit.MONTHLY -> generateMonthlyPeriod(
            startDayOfMonth = recurrenceStartValue,
            currentDate = currentLocalDate,
            endDateInclusive = endDateInclusive
        )
    }
}

/**
 * Generates the daily period based on the current date. The end date is inclusive by default.
 */
fun generateDailyPeriod(
    currentDate: LocalDate = currentLocalDate(), endDateInclusive: Boolean = true
): Period {
    val periodEnd = currentDate.applyIf({ !endDateInclusive }) {
        plus(1, DateTimeUnit.DAY)
    }

    return Period(
        start = currentDate, end = periodEnd
    )
}

/**
 * Generates the weekly period based on the current date and the start day of the week.
 * The end date is inclusive by default.
 */
fun generateWeeklyPeriod(
    startDayOfWeek: Int,
    currentDate: LocalDate = currentLocalDate(),
    endDateInclusive: Boolean = true
): Period {
    require(startDayOfWeek in 1..7) { "startDayOfWeek must be between 1 and 7 for weekly periodicity" }

    val daysToSubtract = with(currentDate.dayOfWeek.value) {
        when {
            this > startDayOfWeek -> this - startDayOfWeek
            this < startDayOfWeek -> 7 - startDayOfWeek + this
            else -> 0
        }
    }

    val periodStart = currentDate.minus(daysToSubtract, DateTimeUnit.DAY)
    val periodEnd = periodStart.plus(6, DateTimeUnit.DAY).applyIf({ !endDateInclusive }) {
        plus(1, DateTimeUnit.DAY)
    }

    return Period(
        start = periodStart, end = periodEnd
    )
}

/**
 * Generates the monthly period based on the current date and the start day of the month.
 * The end date is inclusive by default.
 *
 * TODO: needs testing for edge cases
 */
fun generateMonthlyPeriod(
    startDayOfMonth: Int,
    currentDate: LocalDate = currentLocalDate(),
    endDateInclusive: Boolean = true
): Period {
    require(startDayOfMonth in 1..31) { "startDayOfMonth must be between 1 and 31 for monthly periodicity" }

    val javaLocalDate = currentDate.toJavaLocalDate()

    val periodStart = if (currentDate.dayOfMonth >= startDayOfMonth) {
        val adjustedStartDay = minOf(startDayOfMonth, javaLocalDate.lengthOfMonth())
        javaLocalDate.withDayOfMonth(adjustedStartDay).toKotlinLocalDate()
    } else {
        val previousMonthDate = javaLocalDate.minusMonths(1)
        val adjustedStartDay = minOf(startDayOfMonth, previousMonthDate.lengthOfMonth())
        previousMonthDate.withDayOfMonth(adjustedStartDay).toKotlinLocalDate()
    }

    val nextMonthDate = periodStart.toJavaLocalDate().plusMonths(1)
    val adjustedEndDay = minOf(startDayOfMonth, nextMonthDate.lengthOfMonth())
    val periodEnd = nextMonthDate.withDayOfMonth(adjustedEndDay).toKotlinLocalDate().let {
        if (endDateInclusive) it.minus(1, DateTimeUnit.DAY) else it
    }

    return Period(
        start = periodStart, end = periodEnd
    )
}

/**
 * Generates the next daily period based on the current period.
 * It adds 1 day to the current period start and end dates.
 */
fun Period.nextDaily(): Period {
    return Period(
        start = this.start.plus(1, DateTimeUnit.DAY), end = this.end.plus(1, DateTimeUnit.DAY)
    )
}

/**
 * Generates the previous daily period based on the current period.
 * It subtracts 1 day from the current period start and end dates.
 */
fun Period.previousDaily(): Period {
    return Period(
        start = this.start.minus(1, DateTimeUnit.DAY), end = this.end.minus(1, DateTimeUnit.DAY)
    )
}

/**
 * Generates the next weekly period based on the current period.
 * It adds 1 week to the current period start and end dates.
 */
fun Period.nextWeekly(): Period {
    return Period(
        start = this.start.plus(1, DateTimeUnit.WEEK), end = this.end.plus(1, DateTimeUnit.WEEK)
    )
}

/**
 * Generates the next period based on the current period, recurrence start value, and recurrence unit.
 */
fun Period.nextPeriod(
    currentPeriod: Period,
    recurrenceStartValue: Int,
    recurrenceUnit: RecurrenceUnit,
): Period {
    return when (recurrenceUnit) {
        RecurrenceUnit.DAILY -> currentPeriod.nextDaily()
        RecurrenceUnit.WEEKLY -> currentPeriod.nextWeekly()
        RecurrenceUnit.MONTHLY -> currentPeriod.nextMonthly(startDayOfMonth = recurrenceStartValue)
    }
}

/**
 * Generates the previous period based on the current period, recurrence start value, and recurrence unit.
 */
fun Period.previousPeriod(
    currentPeriod: Period,
    recurrenceStartValue: Int,
    recurrenceUnit: RecurrenceUnit,
): Period {
    return when (recurrenceUnit) {
        RecurrenceUnit.DAILY -> currentPeriod.previousDaily()
        RecurrenceUnit.WEEKLY -> currentPeriod.previousWeekly()
        RecurrenceUnit.MONTHLY -> currentPeriod.previousMonthly(startDayOfMonth = recurrenceStartValue)
    }
}

/**
 * Generates the previous weekly period based on the current period.
 * It subtracts 1 week from the current period start and end dates.
 */
fun Period.previousWeekly(): Period {
    return Period(
        start = this.start.minus(1, DateTimeUnit.WEEK), end = this.end.minus(1, DateTimeUnit.WEEK)
    )
}

/**
 * Generates the next monthly period based on the current period.
 * It adds 1 month to the current period start and end dates. Adjusts the day of the month if necessary based on the month length.
 *
 * TODO: needs testing for edge cases
 */
fun Period.nextMonthly(startDayOfMonth: Int): Period {
    require(startDayOfMonth in 1..31) { "startDayOfMonth must be between 1 and 31" }

    val nextStart = this.start
        .toJavaLocalDate()
        .plusMonths(1)
        .let { nextMonthDate ->
            val adjustedDay = minOf(startDayOfMonth, nextMonthDate.lengthOfMonth())
            nextMonthDate.withDayOfMonth(adjustedDay).toKotlinLocalDate()
        }

    val nextEnd = nextStart
        .toJavaLocalDate()
        .plusMonths(1)
        .let { followingMonthDate ->
            val adjustedDay = minOf(startDayOfMonth, followingMonthDate.lengthOfMonth())
            followingMonthDate.withDayOfMonth(adjustedDay).toKotlinLocalDate()
        }.minus(1, DateTimeUnit.DAY)

    return Period(start = nextStart, end = nextEnd)
}

/**
 * Generates the previous monthly period based on the current period.
 * It subtracts 1 month from the current period start and end dates. Adjusts the day of the month if necessary based on the month length.
 *
 * TODO: needs testing for edge cases
 */
fun Period.previousMonthly(startDayOfMonth: Int): Period {
    require(startDayOfMonth in 1..31) { "startDayOfMonth must be between 1 and 31" }

    val previousStart = this.start
        .toJavaLocalDate()
        .minusMonths(1)
        .let { previousMonthDate ->
            val adjustedDay = minOf(startDayOfMonth, previousMonthDate.lengthOfMonth())
            previousMonthDate.withDayOfMonth(adjustedDay).toKotlinLocalDate()
        }

    val previousEnd = this.start.minus(1, DateTimeUnit.DAY)

    return Period(start = previousStart, end = previousEnd)
}