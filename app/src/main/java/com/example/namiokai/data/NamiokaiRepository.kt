package com.example.namiokai.data

import com.example.namiokai.model.User
import kotlinx.coroutines.flow.Flow

interface NamiokaiRepository {
    suspend fun getUsers(): Flow<List<User>>
    suspend fun getUser(uid: String): Flow<User>
}