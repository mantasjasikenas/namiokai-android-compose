package com.github.mantasjasikenas.core.domain.model.bills

import kotlinx.serialization.Serializable

@Serializable
data class BillFormArgs(
    val navigatedFrom: String? = null,
    val billType: BillType? = null,
    val billId: String? = null,
)