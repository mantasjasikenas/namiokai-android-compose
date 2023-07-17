package com.github.mantasjasikenas.namiokai.ui.screens.bill

import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.utils.Filter


data class BillUiState(
    val purchaseBills: List<PurchaseBill> = emptyList(),
    val filteredPurchaseBills: List<PurchaseBill> = emptyList(),
    val appliedFilters: Map<String, Filter<PurchaseBill>> = emptyMap(),
)