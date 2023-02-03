package com.github.mantasjasikenas.namiokai.data.repository.debts

import android.util.Log
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

private const val TAG = "DebtsManager"

class DebtsManager @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    private val debtsRepo = DebtsRepository()

    init {
        Log.d(TAG, "Initiated")
    }

    suspend fun getDebts(): Flow<DebtsMap> = channelFlow {
        firebaseRepository.getBillsAndFuel().collect { (bills, fuels) ->
                val debts = debtsRepo.calculateDebts(bills, fuels)
                send(debts)
            }

        awaitClose {
            Log.d(TAG, "Closed getDebts channel flow")
        }
    }

}