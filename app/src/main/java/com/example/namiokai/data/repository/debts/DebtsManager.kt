package com.example.namiokai.data.repository.debts

import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.data.UsersRepository
import com.example.namiokai.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


class DebtsManager @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val debtsRepo = DebtsRepository()

    val debtsChannelFlow: Flow<DebtsHashMap> = channelFlow {
        coroutineScope.launch {
            combine(
                firebaseRepository.getUsers(),
                firebaseRepository.getBills(),
                firebaseRepository.getFuel()
            ) { users, bills, fuels ->
                Triple(users, bills, fuels)
            }.collect { (users, bills, fuels) ->
                val debts = debtsRepo.calculateDebts(users, bills, fuels)
                send(debts)
            }
        }

        awaitClose {
            // FIXME fix it
            coroutineScope.cancel()
            println("Closed")
        }
    }


    init {
        println("Init")
    }




}