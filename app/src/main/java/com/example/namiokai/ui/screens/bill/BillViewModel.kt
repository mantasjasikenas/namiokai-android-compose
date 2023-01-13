package com.example.namiokai.ui.screens.bill

import android.annotation.SuppressLint
import android.util.Log
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
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

private const val TAG = "BILL_VM"

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

    @SuppressLint("SimpleDateFormat")
    fun insertBill(bill: Bill) {

        val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val currentDate = format.format(Date())

        bill.date = currentDate
        Log.d(TAG, bill.toString())

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertBill(bill)
            }
        }
    }
}