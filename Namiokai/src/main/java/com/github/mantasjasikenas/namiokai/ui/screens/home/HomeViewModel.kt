package com.github.mantasjasikenas.namiokai.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.repository.debts.DebtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val debtsManager: DebtsManager
) : ViewModel() {

    private val _debtsUiState = MutableStateFlow(HomeUiState())
    val debtsUiState = _debtsUiState.asStateFlow()

    init {
        getUserDebts()
    }

    private fun getUserDebts() {
        viewModelScope.launch {
            debtsManager.getDebtsSync()
        }
    }

}
