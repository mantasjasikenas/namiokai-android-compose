package com.github.mantasjasikenas.namiokai.data.repository.debts

import android.util.Log
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.di.annotations.ApplicationScope
import com.github.mantasjasikenas.namiokai.model.FlatBill
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DebtsManager"

class DebtsManager @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    @ApplicationScope private val coroutineScope: CoroutineScope
) {
    private val debtsRepo = DebtsRepository()
    private lateinit var cachedDebts: DebtsMap
    private lateinit var cachedFlatBills: List<FlatBill>



    init {
        coroutineScope.launch {
            getDebts().collect { debts ->
                cachedDebts = debts
            }
        }
        coroutineScope.launch {
            getFlatBill().collect { flatBill ->
                cachedFlatBills = flatBill
            }
        }
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

    suspend fun getFlatBill(): Flow<List<FlatBill>> = channelFlow {
        firebaseRepository.getFlatBills().collect { bill ->
            send(bill)
        }

        awaitClose {
            Log.d(TAG, "Closed getFlatBill channel flow")
        }
    }

    fun getDebtsSync(): DebtsMap {
        return cachedDebts
    }

    fun getFlatBillSync(): List<FlatBill> {
        return cachedFlatBills
    }

}