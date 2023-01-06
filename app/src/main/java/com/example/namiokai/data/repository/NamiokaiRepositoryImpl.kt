package com.example.namiokai.data.repository

import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.data.NamiokaiRepository
import javax.inject.Inject


class NamiokaiRepositoryImpl @Inject constructor(private val firebaseRepository: FirebaseRepository) :
    NamiokaiRepository {

    override suspend fun getUsers() = firebaseRepository.getUsers()

    override suspend fun getUser(uid: String) = firebaseRepository.getUser(uid)


}