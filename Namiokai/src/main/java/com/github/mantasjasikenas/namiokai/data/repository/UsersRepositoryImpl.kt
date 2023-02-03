package com.github.mantasjasikenas.namiokai.data.repository

import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(private val firebaseRepository: FirebaseRepository) :
    UsersRepository {

    override suspend fun getUsers() = firebaseRepository.getUsers()

    override suspend fun getUser(uid: String) = firebaseRepository.getUser(uid)

}