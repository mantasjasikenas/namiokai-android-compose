package com.example.namiokai.model

data class Fuel(
    var date: String = "",
    var driverUid: String = "",
    var passengersUid: List<String> = emptyList(),
    var tripDestination: String = "",
    var tripPricePerUser: Double = 0.0
)

fun Fuel.isValid(): Boolean {
    return passengersUid.isNotEmpty() && tripDestination.isNotEmpty() && driverUid.isNotEmpty()
}

/*fun Fuel.tripPricePerUser(): Double {
    return when (passengersUid.count()) {
        1 -> 7.0
        else -> 5.0
    }
}*/




