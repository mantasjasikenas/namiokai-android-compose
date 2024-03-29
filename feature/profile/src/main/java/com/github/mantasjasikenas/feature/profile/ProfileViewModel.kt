package com.github.mantasjasikenas.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.repository.AuthRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    usersRepository: UsersRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val profileUiState: StateFlow<ProfileUiState> =
        usersRepository.currentUser
            .map { user ->
                ProfileUiState.Success(
                    currentUser = user
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProfileUiState.Loading
            )

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

}


sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(
        val currentUser: User
    ) : ProfileUiState
}