package com.github.mantasjasikenas.namiokai.ui.screens.fuel

import com.github.mantasjasikenas.namiokai.model.Destination
import com.github.mantasjasikenas.namiokai.model.Fuel

data class FuelUiState(
    val fuels: List<Fuel> = emptyList(),
    val destinations: List<Destination> = emptyList()
)