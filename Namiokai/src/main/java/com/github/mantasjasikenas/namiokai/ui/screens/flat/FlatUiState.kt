package com.github.mantasjasikenas.namiokai.ui.screens.flat

import com.github.mantasjasikenas.namiokai.model.Filter
import com.github.mantasjasikenas.namiokai.model.bills.FlatBill

data class FlatUiState(
    val flatBills: List<FlatBill> = emptyList(),
    val filteredFlatBills: List<FlatBill> = emptyList(),
    val filters: List<Filter<FlatBill, Any>> = emptyList(),
)
