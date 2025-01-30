package com.github.mantasjasikenas.core.domain.model.bills

import com.github.mantasjasikenas.core.common.util.UserUid
import com.github.mantasjasikenas.core.common.util.format

interface Bill {
    val documentId: String
    var date: String
    var createdByUid: String
    var paymasterUid: String
    var splitUsersUid: List<String>
    var spaceId: String
    val total: Double

    fun splitPricePerUser(): Double
    fun isValid(): Boolean
}

fun Bill.resolveBillCost(currentUserUid: UserUid): String {
    val totalCost = this.total
    val splitPricePerUser = this.splitPricePerUser()

    val isCurrentUserPaymaster = this.paymasterUid == currentUserUid
    val isCurrentUserInSplitUsers = this.splitUsersUid.any { it == currentUserUid }

    return if (!isCurrentUserPaymaster && isCurrentUserInSplitUsers) {
        "-${splitPricePerUser.format(2)}"
    } else if (isCurrentUserPaymaster && !isCurrentUserInSplitUsers) {
        "+${totalCost.format(2)}"
    } else if (isCurrentUserPaymaster) {
        "+${(totalCost - splitPricePerUser).format(2)}"
    } else {
        totalCost.format(2)
    }
}

fun Bill.formatDateTime(): String {
    return this.date.replace("T", " ")
}