package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal const val SPACE_COLLECTION = "spaces"
internal const val SPACE_MEMBERS_FIELD = "memberIds"

class SpaceRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
) : SpaceRepository {
    private val spacesCollection = db.collection(SPACE_COLLECTION)

    override fun getSpaceDocumentReference(spaceId: String) = spacesCollection.document(spaceId)

    override suspend fun createSpace(space: Space) {
        spacesCollection.add(space)
    }

    override suspend fun getSpace(spaceId: String): Flow<Space?> {
        return getSpaceDocumentReference(spaceId)
            .snapshots()
            .map {
                it.toObject<Space>()
            }
    }

    override suspend fun getSpacesByUser(userId: String): Flow<List<Space>> =
        spacesCollection.whereArrayContains(SPACE_MEMBERS_FIELD, userId).snapshots()
            .map { snapshot ->
                snapshot.documents.map { it.toObject<Space>()!! }
            }

    override suspend fun addUserToSpace(spaceId: String, userId: String) {
        val spaceRef = spacesCollection.document(spaceId)

        db.runTransaction { transaction ->
            val space = transaction.get(spaceRef).toObject<Space>()

            if (space != null && !space.memberIds.contains(userId)) {
                val updatedUserIds = space.memberIds.toMutableList().apply { add(userId) }

                transaction.update(spaceRef, SPACE_MEMBERS_FIELD, updatedUserIds)
            }
        }.await()
    }

    override suspend fun removeUserFromSpace(spaceId: String, userId: String) {
        val spaceRef = spacesCollection.document(spaceId)

        db.runTransaction { transaction ->
            val space = transaction.get(spaceRef).toObject<Space>()

            if (space != null && space.memberIds.contains(userId)) {
                val updatedUserIds = space.memberIds.toMutableList().apply { remove(userId) }

                transaction.update(spaceRef, SPACE_MEMBERS_FIELD, updatedUserIds)
            }
        }.await()
    }

    override suspend fun deleteSpace(spaceId: String) {
        spacesCollection.document(spaceId).delete()
    }

    override suspend fun updateSpace(space: Space) {
        if (space.spaceId.isNotEmpty()) {
            spacesCollection.document(space.spaceId).set(space)
        }
    }
}