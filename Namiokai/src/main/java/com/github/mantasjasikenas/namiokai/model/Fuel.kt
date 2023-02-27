package com.github.mantasjasikenas.namiokai.model

import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class Fuel(
    @DocumentId
    val documentId: String = "",
    var date: String = "",
    var driverUid: String = "",
    var passengersUid: List<String> = emptyList(),
    var tripDestination: String = "",
    var tripPricePerUser: Double = 0.0,
    var createdByUid : String = "",
)

fun Fuel.isValid(): Boolean {
    return passengersUid.isNotEmpty() && tripDestination.isNotEmpty() && driverUid.isNotEmpty()
}
