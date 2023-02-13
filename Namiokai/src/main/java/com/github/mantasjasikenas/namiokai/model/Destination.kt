package com.github.mantasjasikenas.namiokai.model

import kotlinx.serialization.Serializable

@Serializable
data class Destination(
    var name: String = "",
    val tripPriceAlone: Double = 7.0,
    val tripPriceWithOthers: Double = 5.0
)
