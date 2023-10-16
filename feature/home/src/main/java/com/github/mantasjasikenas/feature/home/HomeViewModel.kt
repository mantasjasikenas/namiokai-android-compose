package com.github.mantasjasikenas.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.DebtsMap
import com.github.mantasjasikenas.core.data.repository.debts.DebtsManager
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    debtsManager: DebtsManager,
    usersRepository: UsersRepository,
    periodRepository: PeriodRepository
) : ViewModel() {

    val homeUiState: StateFlow<HomeUiState> =
        periodRepository.currentPeriod
            .map { period ->
                HomeUiState.Success(
                    currentPeriod = period
                )
            }
            .combine(debtsManager.getCurrentPeriodDebts()) { uiState, debts ->
                uiState.copy(
                    debts = debts
                )
            }
            .combine(usersRepository.currentUser) { uiState, user ->
                uiState.copy(
                    currentUser = user
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
        val debts: DebtsMap = mutableMapOf(),
        val currentPeriod: Period = Period(),
        val currentUser: User = User(),
        val lastUpdated: LocalDateTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
    ) : HomeUiState
}
