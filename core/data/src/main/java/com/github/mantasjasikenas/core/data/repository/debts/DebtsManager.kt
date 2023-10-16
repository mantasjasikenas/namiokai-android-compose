package com.github.mantasjasikenas.core.data.repository.debts

import android.util.Log
import com.github.mantasjasikenas.core.common.di.annotations.ApplicationScope
import com.github.mantasjasikenas.core.common.util.DebtsMap
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

private const val TAG = "DebtsManager"

class DebtsManager @Inject constructor(
    private val billsRepository: BillsRepository,
    private val periodRepository: PeriodRepository,
    @ApplicationScope private val coroutineScope: CoroutineScope
) {
    private val debtsRepository = DebtsRepository()


    fun getDebts(): Flow<DebtsMap> = channelFlow {
        billsRepository.getBills()
            .collect { bills ->
                val debts = debtsRepository.calculateDebts(bills)
                send(debts)
            }

        awaitClose {
            Log.d(
                TAG,
                "Closed getDebts channel flow"
            )
        }
    }

    private fun getDebts(period: Period): Flow<DebtsMap> = channelFlow {
        billsRepository.getBills(period)
            .collect { bills ->
                val debts = debtsRepository.calculateDebts(bills)
                send(debts)
            }

        awaitClose {
            Log.d(
                TAG,
                "Closed getDebts channel flow"
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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
            Log.d(
                TAG,
                "Closed getDebts channel flow"
            )
        }
    }

}