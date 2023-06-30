package com.github.mantasjasikenas.namiokai.ui.screens.bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.utils.Constants.DATE_TIME_FORMAT
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
                firebaseRepository.getPurchaseBills().collect { bills ->
                    _billUiState.update { it.copy(purchaseBills = bills) }
                }
            }
        }
    }


    fun insertBill(purchaseBill: PurchaseBill) {
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val currentDateTime = LocalDateTime.now().format(formatter)

        purchaseBill.date = currentDateTime
        purchaseBill.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertBill(purchaseBill)
            }
        }
    }

    fun updateBill(purchaseBill: PurchaseBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.updateBill(purchaseBill)
            }
        }
    }

    fun deleteBill(purchaseBill: PurchaseBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.deleteBill(purchaseBill)
            }
        }
    }
}