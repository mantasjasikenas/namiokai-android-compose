package com.github.mantasjasikenas.feature.debts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.DebtsMap
import com.github.mantasjasikenas.core.data.repository.debts.DebtsManager
import com.github.mantasjasikenas.core.domain.model.Period
import com.github.mantasjasikenas.core.domain.model.PeriodState
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DebtsViewModel @Inject constructor(
    debtsManager: DebtsManager,
    usersRepository: UsersRepository,
    private val periodRepository: PeriodRepository
) : ViewModel() {

    val debtsUiState: StateFlow<DebtsUiState> =
        usersRepository.getUsers()
            .map { users ->
                DebtsUiState.Success(users = users)
            }
            .combine(periodRepository.getPeriodState()) { uiState, periodState ->
                uiState.copy(periodState = periodState)
            }
            .combine(debtsManager.getUserSelectedPeriodDebts()) { uiState, debts ->
                uiState.copy(debts = debts)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DebtsUiState.Loading
            )

    fun onPeriodReset() {
        viewModelScope.launch {
            periodRepository.resetUserSelectedPeriod()
        }
    }

    fun onPeriodUpdate(period: Period) {
        viewModelScope.launch {
            periodRepository.updateUserSelectedPeriod(period)
        }
    }
}

sealed interface DebtsUiState {
    data object Loading : DebtsUiState
    data class Success(
        val currentUser: User = User(),
        val users: List<User> = emptyList(),
        val debts: DebtsMap = HashMap(),
        val periodState: PeriodState = PeriodState()
    ) : DebtsUiState
}
