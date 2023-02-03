package com.github.mantasjasikenas.namiokai.data.repository.auth

import android.util.Log
import com.github.mantasjasikenas.namiokai.data.AuthRepository
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.data.OneTapSignInResponse
import com.github.mantasjasikenas.namiokai.data.SignInWithGoogleResponse
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.utils.Constants.SIGN_IN_REQUEST
import com.github.mantasjasikenas.namiokai.utils.Constants.SIGN_UP_REQUEST
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "AUTH"

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST) private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST) private var signUpRequest: BeginSignInRequest,
    private val firebaseRepository: FirebaseRepository
) : AuthRepository {


    override val isUserAuthenticatedInFirebase = auth.currentUser != null

    override suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Response.Success(signInResult)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                Response.Success(signUpResult)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                Response.Failure(e)
            }
        }
    }

    override suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential
    ): SignInWithGoogleResponse {
        return try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

            if (isNewUser && (authResult.user != null)) {
                firebaseRepository.insertUser(authResult.user!!.toUser())
            }

            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }
}

fun FirebaseUser.toUser(): User = User(
    displayName = displayName ?: "",
    email = email ?: "",
    uid = uid,
    photoUrl = photoUrl?.toString() ?: ""
)



