package com.github.mantasjasikenas.namiokai.ui.screens.home

data class HomeUiState(
    val userDebts: Map<String, Double> = emptyMap(),
    val flatTotal: Double = 0.0,
)
