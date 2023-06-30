package com.github.mantasjasikenas.namiokai.data.repository.debts

import android.util.Log
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.di.annotations.ApplicationScope
import com.github.mantasjasikenas.namiokai.model.Period
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

private const val TAG = "DebtsManager"

class DebtsManager @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    @ApplicationScope private val coroutineScope: CoroutineScope
) {
    private val debtsRepository = DebtsRepository()


    init {
        Log.d(
            TAG,
            "Initiated"
        )
    }

    fun getDebts(): Flow<DebtsMap> = channelFlow {
        firebaseRepository.getBills()
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

    fun getDebts(period: Period): Flow<DebtsMap> = channelFlow {
        firebaseRepository.getBills(period)
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

}