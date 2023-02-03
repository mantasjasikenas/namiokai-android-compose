package com.github.mantasjasikenas.namiokai.data.repository.auth

import com.github.mantasjasikenas.namiokai.data.SignOutResponse
import com.github.mantasjasikenas.namiokai.data.UserRepository
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.User
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient
) : UserRepository {

    override val user: User? = auth.currentUser?.toUser()


    override suspend fun signOut(): SignOutResponse {
        return try {
            auth.signOut()
            oneTapClient.signOut().await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}