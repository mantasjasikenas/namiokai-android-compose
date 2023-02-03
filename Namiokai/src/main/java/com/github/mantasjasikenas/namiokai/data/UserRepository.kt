package com.github.mantasjasikenas.namiokai.data

import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.User

typealias SignOutResponse = Response<Boolean>

interface UserRepository {
    val user: User?

    suspend fun signOut(): SignOutResponse

}