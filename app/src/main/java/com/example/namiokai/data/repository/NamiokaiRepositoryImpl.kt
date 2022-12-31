package com.example.namiokai.data.repository

import com.example.namiokai.data.NamiokaiRepository
import kotlinx.coroutines.flow.update


class NamiokaiRepositoryImpl : NamiokaiRepository {

    override suspend fun getNamiokai(): List<String> =
        listOf("Mantelis", "Klaidelis", "Klaidas")


}