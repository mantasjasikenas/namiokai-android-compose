package com.example.namiokai.ui.screens.bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.model.Bill
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BillViewModel @Inject constructor(private val firebaseRepository: FirebaseRepository) :
    ViewModel() {

    private val _billUiState = MutableStateFlow(BillUiState())
    val uiState = _billUiState.asStateFlow()

    init {
        getBills()
    }

    private fun getBills() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.getBills().collect { bills ->
                    _billUiState.update { it.copy(bills = bills) }
                }
            }
        }
    }

    fun insertBill(bill: Bill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertBill(bill)
            }
        }
    }
}