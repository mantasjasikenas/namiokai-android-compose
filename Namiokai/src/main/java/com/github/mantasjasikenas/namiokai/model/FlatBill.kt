package com.github.mantasjasikenas.namiokai.model

import com.github.mantasjasikenas.namiokai.utils.round
import com.google.firebase.firestore.DocumentId
import kotlinx.serialization.Serializable

@Serializable
data class FlatBill(
    @DocumentId
    var documentId: String = "",
    var paymasterUid: String = "",
    var splitUsersUid: List<String> = emptyList(),
    var paymentDate: String = "",
    var taxesTotal: Double = 0.0,
    var rentTotal: Double = 0.0,
    var createdByUid : String = "",
)

fun FlatBill.isValid(): Boolean {
    return paymasterUid.isNotEmpty() && total() > 0.0 && splitUsersUid.isNotEmpty()
}

fun FlatBill.splitPricePerUser(): Double {
    return (total() / splitUsersUid.count()).round(2)
}

fun FlatBill.total(): Double {
    return taxesTotal + rentTotal
}
