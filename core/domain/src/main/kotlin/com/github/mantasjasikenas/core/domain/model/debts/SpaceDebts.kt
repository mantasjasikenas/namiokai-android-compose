package com.github.mantasjasikenas.core.domain.model.debts

import com.github.mantasjasikenas.core.domain.model.space.Space
import com.github.mantasjasikenas.core.domain.model.period.Period

data class SpaceDebts(
    val space: Space,
    val period: Period,
    val debts: List<Pair<String, Map<String, List<DebtBill>>>> = emptyList(),
    val currentUserDebts: Map<String, List<DebtBill>> = emptyMap(),
)