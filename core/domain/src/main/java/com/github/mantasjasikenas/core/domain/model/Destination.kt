package com.github.mantasjasikenas.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Destination(
    var name: String = "",
    val tripPriceAlone: Double = 7.0,
    val tripPriceWithOthers: Double = 5.0
)
