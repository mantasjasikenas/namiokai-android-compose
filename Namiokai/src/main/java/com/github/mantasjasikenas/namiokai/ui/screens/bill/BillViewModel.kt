package com.github.mantasjasikenas.namiokai.ui.screens.bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.model.Bill
import com.github.mantasjasikenas.namiokai.utils.Constants.DATE_FORMAT_DISPLAY
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "BillViewModel"

@HiltViewModel
class BillViewModel @Inject constructor(private val firebaseRepository: com.github.mantasjasikenas.namiokai.data.FirebaseRepository) :
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
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_DISPLAY)
        val currentDateTime = LocalDateTime.now().format(formatter)

        bill.date = currentDateTime
        bill.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertBill(bill)
            }
        }
    }

    fun updateBill(bill: Bill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.updateBill(bill)
            }
        }
    }

    fun deleteBill(bill: Bill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.deleteBill(bill)
            }
        }
    }
}