package com.example.namiokai.data.repository

import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.data.UsersRepository
import javax.inject.Inject


class UsersRepositoryImpl @Inject constructor(private val firebaseRepository: FirebaseRepository) :
    UsersRepository {

    override suspend fun getUsers() = firebaseRepository.getUsers()

    override suspend fun getUser(uid: String) = firebaseRepository.getUser(uid)


}