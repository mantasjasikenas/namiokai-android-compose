package com.example.namiokai.data.repository

import com.example.namiokai.data.SignOutResponse
import com.example.namiokai.data.UserRepository
import com.example.namiokai.model.Response
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    private var signInClient: GoogleSignInClient,
) : UserRepository {

    override val displayName = auth.currentUser?.displayName.toString()
    override val photoUrl = auth.currentUser?.photoUrl.toString()

    override suspend fun signOut(): SignOutResponse {
        return try {
            oneTapClient.signOut().await()
            auth.signOut()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    /* override suspend fun revokeAccess(): RevokeAccessResponse {
         return try {
             auth.currentUser?.apply {
                 db.collection(USERS).document(uid).delete().await()
                 signInClient.revokeAccess().await()
                 oneTapClient.signOut().await()
                 delete().await()
             }
             Response.Success(true)
         } catch (e: Exception) {
             Response.Failure(e)
         }
     }*/
}