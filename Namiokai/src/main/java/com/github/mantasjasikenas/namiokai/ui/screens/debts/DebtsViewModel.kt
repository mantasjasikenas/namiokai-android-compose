package com.github.mantasjasikenas.namiokai.ui.screens.debts

import androidx.lifecycle.ViewModel
import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsManager
import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsMap
import com.github.mantasjasikenas.namiokai.model.Period
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class DebtsViewModel @Inject constructor(
    private val debtsManager: DebtsManager
) : ViewModel() {

    private val _debtsUiState = MutableStateFlow(DebtsUiState())
    val debtsUiState = _debtsUiState.asStateFlow()

    fun getDebts(period: Period): Flow<DebtsMap> {
        return debtsManager.getDebts(period)
    }

    /*init {
        //getDebts()
    }

    private fun getDebts() {
        viewModelScope.launch {
            debtsManager.getDebts().collect { debts ->
                _debtsUiState.update { it.copy(debts = debts) }
            }
        }
    }*/

}
