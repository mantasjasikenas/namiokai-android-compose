package com.example.namiokai.model

data class Fuel(
    var date: String = "",
    var passengers: List<User> = emptyList(),
    var tripToHome: Boolean = false,
    var tripToKaunas: Boolean = false,
    var driver: User = User()
)

fun Fuel.isValid(): Boolean {
    return passengers.isNotEmpty() && (tripToHome || tripToKaunas) && driver.uid.isNotEmpty()
}

fun Fuel.tripPricePerUser(): Double {
    return when (passengers.count()) {
        1 -> 7.0
        else -> 5.0
    }
}

fun Fuel.tripDestination(): String {
    return when {
        tripToHome -> "Kėdainiai"
        tripToKaunas -> "Kaunas"
        else -> ""
    }
}

