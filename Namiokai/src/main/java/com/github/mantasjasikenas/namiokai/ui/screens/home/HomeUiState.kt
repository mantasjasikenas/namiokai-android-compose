package com.github.mantasjasikenas.namiokai.ui.screens.home

import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsMap
import com.github.mantasjasikenas.namiokai.model.FlatBill
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HomeUiState(
    val debts: DebtsMap = mutableMapOf(),
    val flatBills: List<FlatBill> = emptyList(),
    val lastUpdated : LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
)
