package com.github.mantasjasikenas.namiokai.model

import kotlinx.datetime.LocalDate

data class Period(
    val start: LocalDate,
    val end: LocalDate
) {
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