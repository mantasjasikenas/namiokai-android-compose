package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.Invitation
import com.github.mantasjasikenas.core.domain.model.InvitationStatus
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    suspend fun sendInvitation(invitation: Invitation)
    fun getInvitationsByUser(userId: String): Flow<List<Invitation>>
    suspend fun respondToInvitation(invitationId: String, status: InvitationStatus)
}