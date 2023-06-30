package com.github.mantasjasikenas.namiokai.ui.screens.home

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
class HomeViewModel @Inject constructor(
    private val debtsManager: DebtsManager
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    fun getDebts(period: Period): Flow<DebtsMap> {
        return debtsManager.getDebts(period)
    }
}
