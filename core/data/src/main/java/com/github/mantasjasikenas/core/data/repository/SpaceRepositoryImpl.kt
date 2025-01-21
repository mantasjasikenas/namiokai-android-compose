@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.mantasjasikenas.core.data.repository

import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal const val SPACE_COLLECTION = "spaces"
internal const val SPACE_MEMBERS_FIELD = "memberIds"

class SpaceRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val usersRepository: UsersRepository
) : SpaceRepository {
    private val spacesCollection = db.collection(SPACE_COLLECTION)

    override fun getSpaceDocumentReference(spaceId: String) = spacesCollection.document(spaceId)

    override suspend fun createSpace(space: Space) {
        spacesCollection.add(space)
    }

    override fun getSpace(spaceId: String): Flow<Space?> {
        return getSpaceDocumentReference(spaceId).dataObjects<Space>()
    }

    override fun getCurrentUserSpaces(): Flow<List<Space>> {
        return usersRepository.currentUser.flatMapLatest { user ->
            getSpacesByUser(user.uid)
        }
    }

    override fun getSpacesByUser(userId: String): Flow<List<Space>> {
        return spacesCollection
            .whereArrayContains(SPACE_MEMBERS_FIELD, userId)
            .dataObjects<Space>()
    }

    override suspend fun addUserToSpace(spaceId: String, userId: String) {
        val spaceRef = getSpaceDocumentReference(spaceId)

        db.runTransaction { transaction ->
            val space = transaction.get(spaceRef).toObject<Space>()

            if (space != null && !space.memberIds.contains(userId)) {
                val updatedUserIds = space.memberIds.toMutableList().apply { add(userId) }
                transaction.update(spaceRef, SPACE_MEMBERS_FIELD, updatedUserIds)
            }
        }.await()
    }

    override suspend fun removeUserFromSpace(spaceId: String, userId: String) {
        val spaceRef = getSpaceDocumentReference(spaceId)

        db.runTransaction { transaction ->
            val space = transaction.get(spaceRef).toObject<Space>()

            if (space != null && space.memberIds.contains(userId)) {
                val updatedUserIds = space.memberIds.toMutableList().apply { remove(userId) }
                transaction.update(spaceRef, SPACE_MEMBERS_FIELD, updatedUserIds)
            }
        }.await()
    }

    override suspend fun deleteSpace(spaceId: String) {
        if (spaceId.isEmpty()) {
            return
        }

        getSpaceDocumentReference(spaceId).delete()
    }

    override suspend fun updateSpace(space: Space) {
        if (space.spaceId.isEmpty()) {
            return
        }

        getSpaceDocumentReference(space.spaceId).set(space)
    }
}