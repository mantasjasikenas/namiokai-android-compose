package com.github.mantasjasikenas.core.domain.model.bills

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
enum class BillType {
    Purchase, Trip, Flat
}