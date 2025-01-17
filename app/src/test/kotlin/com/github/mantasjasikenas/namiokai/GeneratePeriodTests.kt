package com.github.mantasjasikenas.namiokai

import com.github.mantasjasikenas.core.domain.model.period.generateDailyPeriod
import com.github.mantasjasikenas.core.domain.model.period.generateMonthlyPeriod
import com.github.mantasjasikenas.core.domain.model.period.generateWeeklyPeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GeneratePeriodTests {
    companion object {
        @JvmStatic
        fun parametersForTestGenerateDailyPeriod(): List<Arguments> {
            val dates = listOf(
                LocalDate(2022, 12, 15),
                LocalDate(2022, 6, 30),
                LocalDate(2022, 1, 1),
            )

            return dates.map {
                Arguments.of(
                    it,
                    it,
                    it
                )
            }
        }

        @JvmStatic
        fun parametersForTestGenerateWeeklyPeriod() = listOf(
            Arguments.of(
                LocalDate(2025, 1, 16),
                DayOfWeek.MONDAY.value,
                LocalDate(2025, 1, 13),
                LocalDate(2025, 1, 19),
            ),
            Arguments.of(
                LocalDate(2025, 1, 21),
                4,
                LocalDate(2025, 1, 16),
                LocalDate(2025, 1, 22),
            ),
            Arguments.of(
                LocalDate(2025, 1, 19),
                1,
                LocalDate(2025, 1, 13),
                LocalDate(2025, 1, 19),
            ),
            Arguments.of(
                LocalDate(2025, 1, 2),
                DayOfWeek.TUESDAY.value,
                LocalDate(2024, 12, 31),
                LocalDate(2025, 1, 6),
            ),
        )

        @JvmStatic
        fun parametersForTestGenerateMonthlyPeriod() = listOf(
            Arguments.of(
                LocalDate(2025, 1, 16),
                1,
                LocalDate(2025, 1, 1),
                LocalDate(2025, 1, 31),
            ),
            Arguments.of(
                LocalDate(2025, 1, 21),
                15,
                LocalDate(2025, 1, 15),
                LocalDate(2025, 2, 14),
            ),
            Arguments.of(
                LocalDate(2025, 1, 19),
                22,
                LocalDate(2024, 12, 22),
                LocalDate(2025, 1, 21)
            ),
            Arguments.of(
                LocalDate(2025, 2, 28),
                30,
                LocalDate(2025, 1, 30),
                LocalDate(2025, 2, 27)
            ),
        )
    }

    @DisplayName("Test generate daily period")
    @ParameterizedTest(name = "currentDate: {0}, expectedStartDate: {1}, expectedEndDay: {2}")
    @MethodSource("parametersForTestGenerateDailyPeriod")
    fun testGenerateDailyPeriod(
        currentDate: LocalDate,
        expectedStartDate: LocalDate,
        expectedEndDay: LocalDate
    ) {
        val period = generateDailyPeriod(
            currentDate = currentDate,
        )

        assertEquals(expectedStartDate, period.start)
        assertEquals(expectedEndDay, period.end)
    }

    @DisplayName("Test generate weekly period")
    @ParameterizedTest
    @MethodSource("parametersForTestGenerateWeeklyPeriod")
    fun testGenerateWeeklyPeriod(
        currentDate: LocalDate,
        startDayOfWeek: Int,
        expectedStartDate: LocalDate,
        expectedEndDay: LocalDate
    ) {
        val period = generateWeeklyPeriod(
            startDayOfWeek = startDayOfWeek,
            currentDate = currentDate,
        )

        assertEquals(expectedStartDate, period.start)
        assertEquals(expectedEndDay, period.end)
    }

    @DisplayName("Test generate monthly period")
    @ParameterizedTest
    @MethodSource("parametersForTestGenerateMonthlyPeriod")
    fun testGenerateMonthlyPeriod(
        currentDate: LocalDate,
        startDayOfMonth: Int,
        expectedStartDate: LocalDate,
        expectedEndDay: LocalDate
    ) {
        val period = generateMonthlyPeriod(
            startDayOfMonth = startDayOfMonth,
            currentDate = currentDate,
        )

        assertEquals(expectedStartDate, period.start)
        assertEquals(expectedEndDay, period.end)
    }
}