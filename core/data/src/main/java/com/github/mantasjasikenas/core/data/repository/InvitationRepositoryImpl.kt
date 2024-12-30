package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Invitation
import com.github.mantasjasikenas.core.domain.model.InvitationStatus
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.repository.InvitationRepository
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal const val INVITATION_COLLECTION = "invitations"
internal const val INVITATION_STATUS_FIELD = "status"
internal const val INVITATION_INVITED_USER_ID_FIELD = "invitedUserId"

class InvitationRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val spaceRepository: SpaceRepository
) : InvitationRepository {
    private val invitationsCollection = db.collection(INVITATION_COLLECTION)

    override suspend fun sendInvitation(invitation: Invitation) {
        invitationsCollection.add(invitation).await()
    }

    override fun getInvitationsByUser(userId: String): Flow<List<Invitation>> =
        invitationsCollection.whereEqualTo(INVITATION_INVITED_USER_ID_FIELD, userId).snapshots()
            .map { snapshot ->
                snapshot.documents.map { it.toObject<Invitation>()!! }
            }

    override suspend fun respondToInvitation(invitationId: String, status: InvitationStatus) {
        val invitationRef = invitationsCollection.document(invitationId)

        db.runTransaction { transaction ->
            val invitation = transaction.get(invitationRef).toObject<Invitation>()

            if (invitation != null) {
                transaction.update(invitationRef, INVITATION_STATUS_FIELD, status)

                if (status == InvitationStatus.ACCEPTED) {
                    val spaceRef = spaceRepository.getSpaceDocumentReference(invitation.spaceId)
                    val space = transaction.get(spaceRef).toObject<Space>()

                    if (space != null && !space.memberIds.contains(invitation.invitedUserId)) {
                        val updatedMembers =
                            space.memberIds.toMutableList().apply { add(invitation.invitedUserId) }

                        transaction.update(spaceRef, SPACE_MEMBERS_FIELD, updatedMembers)
                    }
                }
            }
        }.await()
    }
}