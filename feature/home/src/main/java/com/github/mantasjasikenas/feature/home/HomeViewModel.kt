package com.github.mantasjasikenas.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.data.repository.debts.DebtsService
import com.github.mantasjasikenas.core.domain.model.period.Period
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
    debtsService: DebtsService,
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
            .combine(usersRepository.currentUser) { uiState, user ->
                uiState.copy(
                    currentUser = user
                )
            }
            .combine(debtsService.getCurrentPeriodDebts()) { uiState, debts ->
                val uid = uiState.currentUser.uid

                uiState.copy(
                    owedToYou = debts.getTotalOwedToYou(uid),
                    totalDebt = debts.getTotalDebt(uid),
                    totalDebtsCount = debts.getTotalDebtsCount(uid),
                    lastUpdated = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
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
        val owedToYou: Double = 0.0,
        val totalDebt: Double = 0.0,
        val totalDebtsCount: Int = 0,
        val currentPeriod: Period = Period(),
        val currentUser: User = User(),
        val lastUpdated: LocalDateTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
    ) : HomeUiState
}
