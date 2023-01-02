package com.example.namiokai.data.repository

import com.example.namiokai.data.NamiokaiRepository
import com.example.namiokai.model.User


class NamiokaiRepositoryImpl : NamiokaiRepository {

    override suspend fun getUsers(): List<User> =
        listOf(
            User("Mantelis"),
            User("Klaidas"),
            User("Klaidelis"),
            User("Jurgis")
        )


}