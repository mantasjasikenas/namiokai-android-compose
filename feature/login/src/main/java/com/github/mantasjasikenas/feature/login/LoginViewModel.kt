package com.github.mantasjasikenas.feature.login

import androidx.lifecycle.ViewModel
import com.github.mantasjasikenas.core.domain.model.SignInResult
import com.github.mantasjasikenas.core.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepository: AuthRepository
): ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

}

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)