package com.github.mantasjasikenas.namiokai.model

import com.github.mantasjasikenas.namiokai.utils.round
import kotlinx.serialization.Serializable

@Serializable
data class FlatBill(
    var paymasterUid: String = "",
    var splitUsersUid: List<String> = emptyList(),
    var paymentDate: String = "",
    var taxesTotal: Double = 0.0,
    var rentTotal: Double = 0.0,
)

fun FlatBill.isValid(): Boolean {
    return paymasterUid.isNotEmpty() && paymentDate.isNotEmpty() && total() > 0.0 && splitUsersUid.isNotEmpty()
}

fun FlatBill.splitPricePerUser(): Double {
    return (total() / splitUsersUid.count()).round(2)
}

fun FlatBill.total(): Double {
    return taxesTotal + rentTotal
}
