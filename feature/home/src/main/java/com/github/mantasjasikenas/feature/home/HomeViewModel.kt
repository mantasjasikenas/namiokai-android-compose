package com.github.mantasjasikenas.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState> =
        userDataRepository.sharedState
            .map { sharedState ->
                HomeUiState.Success(
                    currentUser = sharedState.currentUser,
                    sharedState = sharedState
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading
            )
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val currentUser: User = User(),
        val sharedState: SharedState,
        val lastUpdated: LocalDateTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
    ) : HomeUiState
}
