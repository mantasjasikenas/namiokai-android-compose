package com.github.mantasjasikenas.feature.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.domain.model.SignInResult
import com.github.mantasjasikenas.core.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInState())
    val uiState = _uiState.asStateFlow()

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val signInResult = authRepository.signInWithGoogle(context)

            showSignInStateMessage(signInResult)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSignInSuccessful = signInResult.user != null,
                )
            }
        }
    }

    fun resetState() {
        _uiState.update { SignInState() }
    }

    private fun showSignInStateMessage(
        signInResult: SignInResult,
    ) {
        val message = when {
            signInResult.user != null -> context.getString(
                R.string.welcome,
                signInResult.user!!.displayName
            )

            signInResult.errorMessage != null -> signInResult.errorMessage
            else -> context.getString(R.string.sign_in_failed)
        }

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        )
            .show()
    }


}

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val isLoading: Boolean = false,
)