package com.github.mantasjasikenas.namiokai.model.bills

import com.github.mantasjasikenas.namiokai.utils.round
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable

@Serializable
data class FlatBill(
    @DocumentId
    override val documentId: String = "",
    override var paymasterUid: String = "",
    override var splitUsersUid: List<String> = emptyList(),
    override var createdByUid: String = "",
    override var date: String = "",
    var taxesTotal: Double = 0.0,
    var rentTotal: Double = 0.0,
) : Bill {

    @get:Exclude
    override val total: Double
        get() = taxesTotal + rentTotal

    override fun splitPricePerUser(): Double {
        return (total / splitUsersUid.count()).round(2)
    }

    @Exclude
    override fun isValid(): Boolean {
        return paymasterUid.isNotEmpty() && total > 0.0 && splitUsersUid.isNotEmpty()
    }

}

