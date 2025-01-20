@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.mantasjasikenas.feature.debts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.data.repository.debts.DebtsService
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.model.debts.SpaceDebts
import com.github.mantasjasikenas.core.domain.model.period.Period
import com.github.mantasjasikenas.core.domain.model.period.nextPeriod
import com.github.mantasjasikenas.core.domain.model.period.previousPeriod
import com.github.mantasjasikenas.core.domain.repository.PeriodRepository
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue


@HiltViewModel
class DebtsViewModel @Inject constructor(
    debtsService: DebtsService,
    usersRepository: UsersRepository,
    spacesRepository: SpaceRepository,
    private val periodRepository: PeriodRepository
) : ViewModel() {

    private val _periodOffset = MutableStateFlow(0)
    val periodOffset: StateFlow<Int> = _periodOffset

    val debtsUiState: StateFlow<DebtsUiState> =
        combine(
            usersRepository.getUsers(),
            usersRepository.currentUser,
            spacesRepository.getCurrentUserSpaces()
        ) { users, currentUser, spaces ->
            DebtsUiState.Success(
                currentUser = currentUser,
                usersMap = users.associateBy { it.uid },
                spacesToPeriod = spaces.associateWith { space ->
                    space.currentPeriod()
                }
            )

        }.combine(_periodOffset) { uiState, periodOffset ->
            val newPeriodToSpaces = uiState.spacesToPeriod.mapValues { (space, period) ->
                if (periodOffset == 0) {
                    period
                } else if (periodOffset > 0) {
                    (1..periodOffset).fold(period) { acc, _ ->
                        period.nextPeriod(
                            currentPeriod = acc,
                            recurrenceStartValue = space.recurrenceStart,
                            recurrenceUnit = space.recurrenceUnit,
                        )
                    }
                } else {
                    (1..periodOffset.absoluteValue).fold(period) { acc, _ ->
                        period.previousPeriod(
                            currentPeriod = acc,
                            recurrenceStartValue = space.recurrenceStart,
                            recurrenceUnit = space.recurrenceUnit,
                        )
                    }
                }
            }

            uiState.copy(
                spacesToPeriod = newPeriodToSpaces
            )
        }.flatMapLatest { uiState ->
            debtsService.getSpaceDebts(
                currentUserUid = uiState.currentUser.uid,
                spacesToPeriods = uiState.spacesToPeriod
            ).map { spaceDebts ->
                uiState.copy(
                    spacesDebts = spaceDebts
                )
            }
        }.stateIn(
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

    fun onPeriodOffsetUpdate(offset: Int) {
        _periodOffset.update {
            it + offset
        }
    }
}

sealed interface DebtsUiState {
    data object Loading : DebtsUiState
    data class Success(
        val currentUser: User = User(),
        val usersMap: Map<String, User> = emptyMap(),
        val spacesToPeriod: Map<Space, Period> = emptyMap(),
        val spacesDebts: List<SpaceDebts> = emptyList()
    ) : DebtsUiState
}
