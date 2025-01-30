package com.github.mantasjasikenas.core.domain.model.bills

import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable

@Serializable
data class Taxes(
    var electricity: Double = 0.0,
) {
    @Exclude
    fun isValid(): Boolean {
        return electricity > 0.0
    }
}
