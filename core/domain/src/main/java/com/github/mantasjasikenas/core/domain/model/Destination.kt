package com.github.mantasjasikenas.core.domain.model

import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable

@Serializable
data class Destination(
    var name: String = "",
    var tripPriceAlone: Double = 7.0,
    var tripPriceWithOthers: Double = 5.0
){
    @Exclude
    fun isValid(): Boolean {
        return name.isNotBlank()
    }
}
