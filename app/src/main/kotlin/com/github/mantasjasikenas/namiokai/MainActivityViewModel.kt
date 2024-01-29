package com.github.mantasjasikenas.namiokai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.UserData
import com.github.mantasjasikenas.core.domain.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {

    val userDataUiState: StateFlow<UserDataUiState> = userDataRepository.userData
        .map { userData ->
            UserDataUiState.Success(
                userData = userData
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserDataUiState.Loading
        )

    val sharedUiState: StateFlow<SharedUiState> = userDataRepository.sharedState
        .map { sharedState ->
            SharedUiState.Success(
                sharedState = sharedState
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SharedUiState.Loading
        )

    fun isLoading(): Boolean {
        return userDataUiState.value is UserDataUiState.Loading ||
                sharedUiState.value is SharedUiState.Loading
    }
}

sealed interface SharedUiState {
    data object Loading : SharedUiState
    data class Success(
        val sharedState: SharedState
    ) : SharedUiState
}

sealed interface UserDataUiState {
    data object Loading : UserDataUiState
    data class Success(
        val userData: UserData
    ) : UserDataUiState
}