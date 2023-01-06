package com.example.namiokai.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.data.NamiokaiRepository
import com.example.namiokai.model.Bill
import com.example.namiokai.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repo: NamiokaiRepository,
    private val firebaseRepository: FirebaseRepository

) : ViewModel() {

    // UI State
    private val _summaryUiState = MutableStateFlow(SummaryUiState())
    val uiState = _summaryUiState.asStateFlow()


    init {
        viewModelScope.launch {
            repo.getUsers().combine(firebaseRepository.getBills()) { users, bills ->
                users to bills
            }.collect { (users, bills) ->
                _summaryUiState.update { it.copy(users = users) }

                val debts = calculateDebts(users, bills)
                _summaryUiState.update { it.copy(reducedDebts = debts) }
            }

        }
    }

    private fun calculateDebts(
        users: List<User>,
        bills: List<Bill>
    ): HashMap<User, HashMap<User, Double>> {
        val debts = HashMap<User, HashMap<User, Double>>()
        for (user in users) {
            val userDebts = HashMap<User, Double>()
            for (bill in bills) {
                if (bill.paymaster.uid == user.uid) {
                    for (participant in bill.splitUsers) {
                        if (participant.uid != user.uid) {
                            if (userDebts.containsKey(participant)) {
                                userDebts[participant] =
                                    userDebts[participant]!! + bill.total / bill.splitUsers.count()
                            } else {
                                userDebts[participant] = bill.total / bill.splitUsers.count()
                            }
                        }
                    }
                }
            }
            debts[user] = userDebts
        }

        reduceDebts(debts)
        return debts
    }


    private fun reduceDebts(debts: HashMap<User, HashMap<User, Double>>) {
        for (user in debts.keys) {
            for (debt in debts[user]!!.keys) {
                if (debts[debt]!!.containsKey(user)) {
                    if (debts[user]!![debt]!! > debts[debt]!![user]!!) {
                        debts[user]!![debt] = debts[user]!![debt]!! - debts[debt]!![user]!!
                        debts[debt]!![user] = 0.0
                    } else if (debts[user]!![debt]!! < debts[debt]!![user]!!) {
                        debts[debt]!![user] = debts[debt]!![user]!! - debts[user]!![debt]!!
                        debts[user]!![debt] = 0.0
                    } else {
                        debts[user]!![debt] = 0.0
                        debts[debt]!![user] = 0.0
                    }
                }
            }
        }
    }


}
