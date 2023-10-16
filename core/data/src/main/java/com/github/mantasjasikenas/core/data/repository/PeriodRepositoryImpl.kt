package com.github.mantasjasikenas.core.data.repository

import android.util.Log
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.getMonthlyPeriod
import com.github.mantasjasikenas.core.domain.model.previousMonthly
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val TAG = "PeriodRepository"

class PeriodRepositoryImpl @Inject constructor(

) : PeriodRepository {
    override val currentPeriod: Flow<Period> = flowOf(getCurrentPeriod())

    private val _userSelectedPeriod = MutableStateFlow(getCurrentPeriod())

    override val userSelectedPeriod: Flow<Period> =
        _userSelectedPeriod.asStateFlow()


    override fun getPeriods(): Flow<List<Period>> {
        return currentPeriod.map { currentPeriod ->
            generatePeriods(currentPeriod)
        }
    }

    override fun getPeriodState(): Flow<PeriodState> {
        return combine(
            currentPeriod,
            userSelectedPeriod,
            getPeriods()
        ) { currentPeriod, userSelectedPeriod, periods ->
            PeriodState(
                currentPeriod = currentPeriod,
                userSelectedPeriod = userSelectedPeriod,
                periods = periods
            )
        }
    }

    override fun updateUserSelectedPeriod(period: Period) {
        _userSelectedPeriod.update {
            period
        }
    }

    override fun resetUserSelectedPeriod() {
        _userSelectedPeriod.update {
            getCurrentPeriod()
        }
    }


    private fun getCurrentPeriod(): Period {
        return Period.getMonthlyPeriod(getStartDate())
    }

    private fun getStartDate(): Int {
        val value = Firebase.remoteConfig.getLong("period_start_day")
            .toInt()

        if (value in 1..31) {
            return value
        }
        else {
            Log.e(
                TAG,
                "Invalid value for period_start_day: $value"
            )
        }

        return 15
    }

    private fun generatePeriods(currentPeriod: Period): List<Period> {
        val nextPeriodsCount = 0
        val previousPeriodsCount = 3
        val totalPeriodsCount = nextPeriodsCount + previousPeriodsCount + 1

        val periods = (0 until totalPeriodsCount).map {
            currentPeriod.previousMonthly(previousPeriodsCount - it)
        }

        return periods
    }
}