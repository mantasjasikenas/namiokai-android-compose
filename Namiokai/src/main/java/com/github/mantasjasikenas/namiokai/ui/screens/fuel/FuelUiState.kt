package com.github.mantasjasikenas.namiokai.ui.screens.fuel

import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.Filter
import com.github.mantasjasikenas.namiokai.model.bills.TripBill

data class FuelUiState(
    val tripBills: List<TripBill> = emptyList(),
    val destinations: List<Destination> = emptyList(),
    val filteredTripBills: List<TripBill> = emptyList(),
    val filters: List<Filter<TripBill, Any>> = emptyList(),
)