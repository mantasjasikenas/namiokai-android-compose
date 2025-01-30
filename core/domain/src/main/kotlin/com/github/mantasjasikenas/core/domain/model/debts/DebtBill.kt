package com.github.mantasjasikenas.core.domain.model.debts

import com.github.mantasjasikenas.core.domain.model.bills.Bill

data class DebtBill(
    val amount: Double,
    val bill: Bill
)

