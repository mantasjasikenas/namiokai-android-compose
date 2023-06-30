package com.github.mantasjasikenas.namiokai.ui.screens.home

import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsMap
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HomeUiState(
    val debts: DebtsMap = mutableMapOf(),
    val lastUpdated : LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
)
