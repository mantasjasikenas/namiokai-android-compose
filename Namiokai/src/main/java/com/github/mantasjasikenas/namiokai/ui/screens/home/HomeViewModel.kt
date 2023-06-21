package com.github.mantasjasikenas.namiokai.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val debtsManager: DebtsManager
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        getUserDebts()
        getFlatBills()
    }

    private fun getUserDebts() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                debtsManager.getDebts().collect { debts ->
                    _homeUiState.update { it.copy(debts = debts) }
                }
            }
        }
    }

    private fun getFlatBills() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                debtsManager.getFlatBill().collect { flatBills ->
                    _homeUiState.update { it.copy(flatBills = flatBills) }
                }
            }
        }
    }

    fun getCurrentPeriod(): Pair<LocalDate, LocalDate> {

        val startDayInclusive = 22

        val periodStart: LocalDate
        val periodEnd: LocalDate

        val currentDate = Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()
        ).date

        if (currentDate.dayOfMonth < startDayInclusive) {
            periodStart = LocalDate(currentDate.year, currentDate.monthNumber - 1, startDayInclusive)
            periodEnd = LocalDate(currentDate.year, currentDate.monthNumber, startDayInclusive - 1)
        } else {
            periodStart = LocalDate(currentDate.year, currentDate.monthNumber, startDayInclusive)
            periodEnd = LocalDate(currentDate.year, currentDate.monthNumber + 1, startDayInclusive - 1)
        }




        return Pair(periodStart, periodEnd)
    }

}
