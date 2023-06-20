package com.github.mantasjasikenas.namiokai.model

import com.google.firebase.firestore.DocumentId
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class BillDto(
    @DocumentId
    val documentId: String = "",
    var date: LocalDateTime = LocalDateTime(0, 1, 1, 0, 0, 0),
    var paymasterUid: String = "",
    var shoppingList: String = "",
    var total: Double = 0.0,
    var splitUsersUid: List<String> = emptyList(),
    var createdByUid : String = "",
)