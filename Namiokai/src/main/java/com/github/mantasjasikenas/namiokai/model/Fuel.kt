package com.github.mantasjasikenas.namiokai.model

import kotlinx.serialization.Serializable

@Serializable
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
