package com.example.namiokai.ui.screens.summary

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
class SummaryViewModel @Inject constructor(
    private val repo: UsersRepository,
    private val firebaseRepository: FirebaseRepository,
    private val debtsManager: DebtsManager

) : ViewModel() {

    // UI State
    private val _summaryUiState = MutableStateFlow(SummaryUiState())
    val uiState = _summaryUiState.asStateFlow()


    init {
        viewModelScope.launch {
            debtsManager.debtsChannelFlow.collect { debts ->
                _summaryUiState.update { it.copy(debts = debts) }
            }
        }
    }


}
