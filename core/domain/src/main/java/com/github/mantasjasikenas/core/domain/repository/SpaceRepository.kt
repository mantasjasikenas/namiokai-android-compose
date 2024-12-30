package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.Space
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface SpaceRepository {
    fun getSpaceDocumentReference(spaceId: String): DocumentReference
    suspend fun createSpace(space: Space)
    suspend fun getSpace(spaceId: String): Flow<Space?>
    suspend fun getSpacesByUser(userId: String): Flow<List<Space>>
    suspend fun addUserToSpace(spaceId: String, userId: String)
    suspend fun removeUserFromSpace(spaceId: String, userId: String)
    suspend fun deleteSpace(spaceId: String)
    suspend fun updateSpace(space: Space)
}