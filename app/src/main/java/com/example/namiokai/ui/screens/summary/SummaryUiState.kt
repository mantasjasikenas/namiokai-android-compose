package com.example.namiokai.ui.screens.summary

import com.example.namiokai.model.User

data class SummaryUiState(
    val users: List<User> = emptyList(),
    val debts: HashMap<User, HashMap<User, Double>> = HashMap(),
    val reducedDebts: HashMap<User, HashMap<User, Double>> = HashMap()
)
