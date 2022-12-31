package com.example.namiokai.data

interface NamiokaiRepository {
    suspend fun getNamiokai(): List<String>
}