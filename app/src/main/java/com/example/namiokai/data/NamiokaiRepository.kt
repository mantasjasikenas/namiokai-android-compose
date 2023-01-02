package com.example.namiokai.data

import com.example.namiokai.model.User

interface NamiokaiRepository {
    suspend fun getUsers(): List<User>
}