package com.example.namiokai.model

data class Fuel(
    var isValid: Boolean = false,
    var date: String = "",
    var passengers: List<User> = emptyList(),
    var tripToHome: Boolean = false,
    var tripToKaunas: Boolean = false,
    var driver: User = User()
)

