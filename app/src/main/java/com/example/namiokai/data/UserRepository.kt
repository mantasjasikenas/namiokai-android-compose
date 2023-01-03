package com.example.namiokai.data

import com.example.namiokai.model.Response

typealias SignOutResponse = Response<Boolean>
typealias RevokeAccessResponse = Response<Boolean>

interface UserRepository {
    val displayName: String
    val photoUrl: String

    suspend fun signOut(): SignOutResponse


}