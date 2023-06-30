package com.github.mantasjasikenas.namiokai.model.bills

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable

@Serializable
data class TripBill(
    @DocumentId
    override val documentId: String = "",
    override var date: String = "",
    override var createdByUid: String = "",
    @get:PropertyName("driverUid")
    @set:PropertyName("driverUid")
    override var paymasterUid: String = "",
    @get:PropertyName("passengersUid")
    @set:PropertyName("passengersUid")
    override var splitUsersUid: List<String> = emptyList(),
    var tripDestination: String = "",
    var tripPricePerUser: Double = 0.0,
) : Bill {

    @get:Exclude
    override val total: Double
        get() = tripPricePerUser * (splitUsersUid.size + if (splitUsersUid.contains(paymasterUid)) 0 else 1)

    override fun splitPricePerUser(): Double {
        return tripPricePerUser
    }

    @Exclude
    override fun isValid(): Boolean {
        return splitUsersUid.isNotEmpty() && tripDestination.isNotEmpty() && paymasterUid.isNotEmpty()
    }
}



