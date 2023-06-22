package com.github.mantasjasikenas.namiokai.ui.screens.debts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DebtsViewModel @Inject constructor(
    private val debtsManager: DebtsManager
) : ViewModel() {

    private val _debtsUiState = MutableStateFlow(DebtsUiState())
    val debtsUiState = _debtsUiState.asStateFlow()

    init {
        getDebts()
        getFlatBills()
    }

    private fun getDebts() {
        viewModelScope.launch {
            debtsManager.getDebts().collect { debts ->
                _debtsUiState.update { it.copy(debts = debts) }
            }
        }
    }

    private fun getFlatBills() {
        viewModelScope.launch {
            debtsManager.getFlatBill().collect { flatBills ->
                _debtsUiState.update { it.copy(flatBills = flatBills) }
            }
        }
    }

}
