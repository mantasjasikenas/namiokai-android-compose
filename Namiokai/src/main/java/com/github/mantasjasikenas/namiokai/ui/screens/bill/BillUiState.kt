package com.github.mantasjasikenas.namiokai.ui.screens.bill

import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.model.Filter


data class BillUiState(
    val purchaseBills: List<PurchaseBill> = emptyList(),
    val filteredPurchaseBills: List<PurchaseBill> = emptyList(),
    val filters: List<Filter<PurchaseBill, Any>> = emptyList(),
)