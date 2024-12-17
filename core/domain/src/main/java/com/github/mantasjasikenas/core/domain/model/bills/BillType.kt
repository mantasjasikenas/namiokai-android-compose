package com.github.mantasjasikenas.core.domain.model.bills

import kotlinx.serialization.Serializable

@Serializable
enum class BillType {
    Purchase, Trip, Flat
}