package com.github.mantasjasikenas.namiokai.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.model.Response
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: com.github.mantasjasikenas.namiokai.data.AuthRepository,
    val oneTapClient: SignInClient,
    private val userRepository: com.github.mantasjasikenas.namiokai.data.UserRepository
) : ViewModel() {

    val isUserAuthenticated
        get() = repo.isUserAuthenticatedInFirebase

    var oneTapSignInResponse by mutableStateOf<com.github.mantasjasikenas.namiokai.data.OneTapSignInResponse>(Response.Success(null))
        private set
    var signInWithGoogleResponse by mutableStateOf<com.github.mantasjasikenas.namiokai.data.SignInWithGoogleResponse>(Response.Success(false))
        private set
    var signOutResponse by mutableStateOf<com.github.mantasjasikenas.namiokai.data.SignOutResponse>(Response.Success(false))
        private set

    fun oneTapSignIn() = viewModelScope.launch {
        oneTapSignInResponse = Response.Loading
        oneTapSignInResponse = repo.oneTapSignInWithGoogle()
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        oneTapSignInResponse = Response.Loading
        signInWithGoogleResponse = repo.firebaseSignInWithGoogle(googleCredential)
    }

    fun signOut() = viewModelScope.launch {
        signOutResponse = Response.Loading
        signOutResponse = userRepository.signOut()
    }
}