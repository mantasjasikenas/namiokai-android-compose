package com.github.mantasjasikenas.core.domain.model.bills

import com.github.mantasjasikenas.core.common.util.round
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseBill(
    @DocumentId
    override val documentId: String = "",
    override var date: String = "",
    override var paymasterUid: String = "",
    override var total: Double = 0.0,
    override var splitUsersUid: List<String> = emptyList(),
    override var createdByUid: String = "",
    override var spaceId: String = "",
    var shoppingList: String = "",
) : Bill {

    override fun splitPricePerUser(): Double {
        return (total / splitUsersUid.count()).round(2)
    }

    @Exclude
    override fun isValid(): Boolean {
        return paymasterUid.isNotEmpty() && spaceId.isNotEmpty() && shoppingList.isNotEmpty() && total > 0.0 && splitUsersUid.isNotEmpty()
    }
}






