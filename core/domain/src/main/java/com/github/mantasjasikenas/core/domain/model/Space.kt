package com.github.mantasjasikenas.core.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.serialization.Serializable

@Serializable
data class Space(
    @DocumentId
    val spaceId: String = "",
    var spaceName: String = "",
    var memberIds: List<String> = emptyList(),
    var createdBy: String = ""
) {
    @Exclude
    fun isValid(): Boolean {
        return memberIds.isNotEmpty() && spaceName.isNotBlank()
    }
}

data class Invitation(
    @DocumentId
    val invitationId: String = "",
    val spaceId: String = "",
    val invitedUserId: String = "",
    val invitingUserId: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING
)

enum class InvitationStatus {
    PENDING, ACCEPTED, DECLINED
}