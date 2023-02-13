package com.github.mantasjasikenas.namiokai.model

import com.github.mantasjasikenas.namiokai.utils.round
import kotlinx.serialization.Serializable

@Serializable
data class Bill(
    var date: String = "",
    var paymasterUid: String = "",
    var shoppingList: String = "",
    var total: Double = 0.0,
    var splitUsersUid: List<String> = emptyList()
)

fun Bill.isValid(): Boolean {
    return paymasterUid.isNotEmpty() && shoppingList.isNotEmpty() && total > 0.0 && splitUsersUid.isNotEmpty()
}

fun Bill.splitPricePerUser(): Double {
    return (total / splitUsersUid.count()).round(2)
}



