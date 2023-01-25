package com.example.namiokai.ui.screens.debts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.data.UsersRepository
import com.example.namiokai.data.repository.debts.DebtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DebtsViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val firebaseRepository: FirebaseRepository,
    private val debtsManager: DebtsManager

) : ViewModel() {

    private val _debtsUiState = MutableStateFlow(DebtsUiState())
    val uiState = _debtsUiState.asStateFlow()

    init {
        viewModelScope.launch {
            debtsManager.getDebts().collect { debts ->
                _debtsUiState.update { it.copy(debts = debts) }
            }
        }
    }
}
