package com.github.mantasjasikenas.namiokai.model.bills

import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.utils.format

interface Bill {
    val documentId: String
    var date: String
    var createdByUid: String
    var paymasterUid: String
    var splitUsersUid: List<String>
    val total: Double

    fun splitPricePerUser(): Double

    fun isValid(): Boolean
}

fun Bill.resolveBillCost(currentUser: User): String {
    val totalCost = this.total
    val splitPricePerUser = this.splitPricePerUser()


    val isCurrentUserPaymaster = this.paymasterUid == currentUser.uid
    val isCurrentUserInSplitUsers = this.splitUsersUid.any { it == currentUser.uid }


    return if (!isCurrentUserPaymaster && isCurrentUserInSplitUsers) {
        "-${splitPricePerUser.format(2)}"
    } else if (isCurrentUserPaymaster && !isCurrentUserInSplitUsers) {
        "+${totalCost.format(2)}"
    } else if (isCurrentUserPaymaster) {
        "+${(totalCost - splitPricePerUser).format(2)}"
    } else {
        "0.00"
    }
}