package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.model.debts.DebtsMap

interface DebtsRepository {
    fun calculateDebts(bills: List<Bill>): DebtsMap?
}