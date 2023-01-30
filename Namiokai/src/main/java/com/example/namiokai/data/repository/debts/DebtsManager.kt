package com.example.namiokai.data.repository.debts

import android.util.Log
import com.example.namiokai.data.FirebaseRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

private const val TAG = "DebtsManager"

class DebtsManager @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    private val debtsRepo = DebtsRepository()

    suspend fun getDebts(): Flow<DebtsMap> =
        channelFlow {
            firebaseRepository.getCollections()
                .collect { (users, bills, fuels) ->
                    val debts = debtsRepo.calculateDebts(users, bills, fuels)
                    send(debts)
                }

            awaitClose {
                Log.d(TAG, "Closed getDebts channel flow")
            }
        }


    init {
        Log.d(TAG, "Init DebtsManager")
    }


}