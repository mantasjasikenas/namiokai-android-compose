package com.example.namiokai.data

import com.example.namiokai.model.Response
import com.example.namiokai.model.User

typealias SignOutResponse = Response<Boolean>
typealias RevokeAccessResponse = Response<Boolean>

interface UserRepository {
    val user: User?

    suspend fun signOut(): SignOutResponse


}