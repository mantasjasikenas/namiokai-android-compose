package com.github.mantasjasikenas.core.domain.model.space

import com.google.firebase.firestore.DocumentId

data class Invitation(
    @DocumentId
    val invitationId: String = "",
    val spaceId: String = "",
    val invitedUserId: String = "",
    val invitingUserId: String = "",
    val date: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING
)

