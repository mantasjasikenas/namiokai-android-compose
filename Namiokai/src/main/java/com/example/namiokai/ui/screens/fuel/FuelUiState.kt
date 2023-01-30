package com.example.namiokai.ui.screens.fuel

import com.example.namiokai.model.Destination
import com.example.namiokai.model.Fuel

data class FuelUiState(
    val fuels: List<Fuel> = emptyList(),
    val destinations: List<Destination> = emptyList()
)