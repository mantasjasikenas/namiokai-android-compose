package com.github.mantasjasikenas.namiokai

import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.period.nextDaily
import com.github.mantasjasikenas.core.domain.model.period.nextMonthly
import com.github.mantasjasikenas.core.domain.model.period.nextWeekly
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GenerateNextPeriodTests {
    companion object {
        @JvmStatic
        fun parametersForTestGenerateNextDailyPeriod(): List<Arguments> {
            val dates = listOf(
                LocalDate(2022, 12, 15),
                LocalDate(2022, 6, 30),
                LocalDate(2022, 1, 1),
            )

            return dates.map {
                Arguments.of(
                    Period(it, it),
                    3,
                    listOf(
                        Period(it.plus(1, DateTimeUnit.DAY), it.plus(1, DateTimeUnit.DAY)),
                        Period(it.plus(2, DateTimeUnit.DAY), it.plus(2, DateTimeUnit.DAY)),
                        Period(it.plus(3, DateTimeUnit.DAY), it.plus(3, DateTimeUnit.DAY)),
                    )
                )
            }
        }

        @JvmStatic
        fun parametersForTestGenerateWeeklyPeriod(): List<Arguments> {
            val startDates = listOf(
                LocalDate(2025, 1, 16),
                LocalDate(2025, 1, 21),
                LocalDate(2025, 1, 19),
                LocalDate(2025, 2, 28),
            )

            val endDays = startDates.map {
                it.plus(6, DateTimeUnit.DAY)
            }

            val amountToGenerate = 5

            return startDates.zip(endDays).map { (startDate, endDate) ->
                Arguments.of(
                    Period(startDate, endDate),
                    amountToGenerate,
                    (0..<amountToGenerate).map {
                        Period(
                            startDate.plus(it + 1, DateTimeUnit.WEEK),
                            endDate.plus(it + 1, DateTimeUnit.WEEK)
                        )
                    }
                )
            }

        }

        @JvmStatic
        fun parametersForTestGenerateMonthlyPeriod(): List<Arguments> {
            val startDates = listOf(
                LocalDate(2025, 1, 16),
                LocalDate(2025, 1, 21),
                LocalDate(2025, 1, 19),
                LocalDate(2025, 2, 28),
            )

            val endDays = startDates.map {
                it.plus(1, DateTimeUnit.MONTH)
            }

            val amountToGenerate = 5

            return startDates.zip(endDays).map { (startDate, endDate) ->
                Arguments.of(
                    Period(startDate, endDate),
                    startDate.dayOfMonth,
                    amountToGenerate,
                    (0..<amountToGenerate).map {
                        Period(
                            startDate.plus(it + 1, DateTimeUnit.MONTH),
                            endDate.plus(it + 1, DateTimeUnit.MONTH)
                                .minus(1, DateTimeUnit.DAY)
                        )
                    }
                )
            }
        }
    }

    @DisplayName("Test generate next daily period")
    @ParameterizedTest(name = "currentPeriod: {0}, amountToGenerate: {1}, expectedPeriods: {2}")
    @MethodSource("parametersForTestGenerateNextDailyPeriod")
    fun testGenerateNextDailyPeriod(
        currentPeriod: Period,
        amountToGenerate: Int,
        expectedPeriods: List<Period>
    ) {
        var period: Period = currentPeriod

        (0..<amountToGenerate).forEach {
            period = period.nextDaily()
            assertEquals(expectedPeriods[it], period)
        }
    }

    @DisplayName("Test generate next weekly period")
    @ParameterizedTest(name = "currentPeriod: {0}, amountToGenerate: {1}, expectedPeriods: {2}")
    @MethodSource("parametersForTestGenerateWeeklyPeriod")
    fun testGenerateWeeklyPeriod(
        currentPeriod: Period,
        amountToGenerate: Int,
        expectedPeriods: List<Period>
    ) {
        var period: Period = currentPeriod

        (0..<amountToGenerate).forEach {
            period = period.nextWeekly()
            assertEquals(expectedPeriods[it], period)
        }
    }


    @DisplayName("Test generate next monthly period")
    @ParameterizedTest(name = "currentPeriod: {0}, startDayOfMonth: {1}, amountToGenerate: {2}, expectedPeriods: {3}")
    @MethodSource("parametersForTestGenerateMonthlyPeriod")
    fun testGenerateMonthlyPeriod(
        currentPeriod: Period,
        startDayOfMonth: Int,
        amountToGenerate: Int,
        expectedPeriods: List<Period>
    ) {
        var period: Period = currentPeriod

        (0..<amountToGenerate).forEach {
            period = period.nextMonthly(startDayOfMonth = startDayOfMonth)
            assertEquals(expectedPeriods[it], period)
        }
    }
}