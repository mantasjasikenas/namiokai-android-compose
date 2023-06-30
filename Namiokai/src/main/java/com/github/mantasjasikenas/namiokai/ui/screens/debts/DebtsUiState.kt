package com.github.mantasjasikenas.namiokai.ui.screens.debts

import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsMap
import com.github.mantasjasikenas.namiokai.model.User

data class DebtsUiState(
    val users: List<User> = emptyList(),
    val debts: DebtsMap = HashMap(),
)
