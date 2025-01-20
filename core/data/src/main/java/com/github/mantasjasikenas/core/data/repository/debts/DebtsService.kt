package com.github.mantasjasikenas.core.data.repository.debts

import android.util.Log
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.debts.DebtsMap
import com.github.mantasjasikenas.core.domain.model.debts.SpaceDebts
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.DebtsRepository
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

private const val TAG = "DebtsService"

class DebtsService @Inject constructor(
    private val billsRepository: BillsRepository,
    private val periodRepository: PeriodRepository,
    private val debtsRepository: DebtsRepository
) {
    @Deprecated("Use getSpacesDebts instead")
    fun getCurrentPeriodDebts(): Flow<DebtsMap> = channelFlow {
        periodRepository.currentPeriod
            .collect { period ->
                billsRepository.getBills(period)
                    .collect { bills ->
                        val debts = debtsRepository.calculateDebts(bills)

                        if (debts != null) {
                            send(debts)
                        }
                    }
            }

        awaitClose {
            Log.d(TAG, "Closed getDebts channel flow")
        }
    }

    fun getSpaceDebts(
        currentUserUid: String,
        spacesToPeriods: Map<Space, Period>,
    ): Flow<List<SpaceDebts>> = channelFlow {
        val spaceDebts = spacesToPeriods
            .map { (space, period) ->
                val bills = billsRepository.getBills(period, space.spaceId)
                    .firstOrNull() ?: emptyList()
                val debts = debtsRepository.calculateDebts(bills)

                SpaceDebts(
                    space = space,
                    period = period,
                    debts = debts
                        ?.getAllDebts()
                        ?.toList() ?: emptyList(),
                    currentUserDebts = debts?.getUserDebts(currentUserUid) ?: emptyMap()
                )
            }

        send(spaceDebts)

        awaitClose {
            Log.d(TAG, "Closed getSpaceDebts channel flow")
        }
    }
}