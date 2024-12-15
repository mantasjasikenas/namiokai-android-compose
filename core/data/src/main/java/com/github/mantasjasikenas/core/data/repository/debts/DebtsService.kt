@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.mantasjasikenas.core.data.repository.debts

import android.util.Log
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.debts.DebtsMap
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.DebtsRepository
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

private const val TAG = "DebtsService"

class DebtsService @Inject constructor(
    private val billsRepository: BillsRepository,
    private val periodRepository: PeriodRepository,
    private val debtsRepository: DebtsRepository
) {
    fun getUserSelectedPeriodDebts(): Flow<DebtsMap> =
        periodRepository.userSelectedPeriod
            .flatMapLatest { period ->
                getDebts(period)
            }


    fun getCurrentPeriodDebts(): Flow<DebtsMap> = channelFlow {
        periodRepository.currentPeriod
            .collect { period ->
                billsRepository.getBills(period)
                    .collect { bills ->
                        val debts = debtsRepository.calculateDebts(bills)

                        send(debts)
                    }
            }

        awaitClose {
            Log.d(TAG, "Closed getDebts channel flow")
        }
    }

    private fun getDebts(period: Period): Flow<DebtsMap> = channelFlow {
        billsRepository.getBills(period)
            .collect { bills ->
                val debts = debtsRepository.calculateDebts(bills)
                send(debts)
            }

        awaitClose {
            Log.d(TAG, "Closed getDebts channel flow")
        }
    }

}