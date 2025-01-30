package com.github.mantasjasikenas.core.domain.repository

import com.github.mantasjasikenas.core.domain.model.space.Space
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow

interface SpaceRepository {
    fun getSpaceDocumentReference(spaceId: String): DocumentReference
    fun getSpace(spaceId: String): Flow<Space?>
    fun getSpacesByUser(userId: String): Flow<List<Space>>
    fun getCurrentUserSpaces(): Flow<List<Space>>
    suspend fun createSpace(space: Space)
    suspend fun addUserToSpace(spaceId: String, userId: String)
    suspend fun removeUserFromSpace(spaceId: String, userId: String)
    suspend fun deleteSpace(spaceId: String)
    suspend fun updateSpace(space: Space)
}