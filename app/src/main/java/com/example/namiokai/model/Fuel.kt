package com.example.namiokai.model

data class Fuel(
    val date: String = "",
    val passengers: Set<User> = emptySet(),
    val tripToHome: Boolean = false,
    val tripToKaunas: Boolean = false,
    val isValid: Boolean = false
)

