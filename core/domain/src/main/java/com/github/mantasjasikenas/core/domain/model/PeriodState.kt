package com.github.mantasjasikenas.core.domain.model

import com.github.mantasjasikenas.core.domain.model.period.Period

data class PeriodState(
    val currentPeriod: Period = Period(),
    val userSelectedPeriod: Period = Period(),
    val periods: List<Period> = emptyList(),
)