package com.example.namiokai.ui.screens.debts

import com.example.namiokai.data.repository.debts.DebtsMap
import com.example.namiokai.model.User

data class DebtsUiState(
    val users: List<User> = emptyList(),
    val debts: DebtsMap = HashMap()
)
