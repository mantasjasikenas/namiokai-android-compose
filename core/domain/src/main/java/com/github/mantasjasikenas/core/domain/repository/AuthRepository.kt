package com.github.mantasjasikenas.core.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.github.mantasjasikenas.core.common.util.Constants.SIGN_IN_REQUEST
import com.github.mantasjasikenas.core.domain.model.SignInResult
import com.github.mantasjasikenas.core.domain.model.toUser
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Named

class AuthRepository(
    private val oneTapClient: SignInClient,
    private val usersRepository: UsersRepository,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                signInRequest
            )
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }

        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(
            googleIdToken,
            null
        )
        return try {
            val authResult = auth.signInWithCredential(googleCredentials)
                .await()
            val user = authResult.user
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

            if (isNewUser && authResult.user != null) {
                usersRepository.insertUser(authResult.user!!.toUser())
            }

            SignInResult(
                data = user?.run {
                    toUser()
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut()
                .await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

}