package com.github.mantasjasikenas.core.domain.model

data class PeriodState(
    val currentPeriod: Period = Period(),
    val userSelectedPeriod: Period = Period(),
    val periods: List<Period> = emptyList(),
)