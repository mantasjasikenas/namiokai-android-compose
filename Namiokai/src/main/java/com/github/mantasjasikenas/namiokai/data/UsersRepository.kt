package com.github.mantasjasikenas.namiokai.data

import com.github.mantasjasikenas.namiokai.model.User
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getUser(uid: String): Flow<User>
}