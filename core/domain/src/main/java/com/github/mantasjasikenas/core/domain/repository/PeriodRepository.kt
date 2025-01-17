package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import kotlinx.coroutines.flow.Flow

interface PeriodRepository {

    val currentPeriod: Flow<Period>
    fun getPeriods(): Flow<List<Period>>
    fun updateUserSelectedPeriod(period: Period)

    val userSelectedPeriod: Flow<Period>
    fun getPeriodState(): Flow<PeriodState>
    fun resetUserSelectedPeriod()
}