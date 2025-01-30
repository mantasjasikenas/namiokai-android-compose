package com.github.mantasjasikenas.core.domain.model.period

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class Period(
    val start: LocalDate = LocalDate(1, 1, 1),
    val end: LocalDate = LocalDate(1, 1, 1)
) {
    companion object;

    override fun toString(): String {
        return "$start - $end"
    }
}

fun LocalDate.isBetween(
    start: LocalDate, end: LocalDate
): Boolean {
    return this in start..end
}

fun Period.contains(isoDate: String): Boolean {
    return LocalDateTime
        .parse(isoDate)
        .date
        .isBetween(start, end)
}