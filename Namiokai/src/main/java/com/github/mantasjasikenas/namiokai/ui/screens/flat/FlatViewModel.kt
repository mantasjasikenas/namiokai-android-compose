package com.github.mantasjasikenas.namiokai.ui.screens.flat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.model.FlatBill
import com.github.mantasjasikenas.namiokai.utils.Constants
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

@HiltViewModel
class FlatViewModel @Inject constructor(private val firebaseRepository: FirebaseRepository) :
    ViewModel() {

    private val _flatUiState = MutableStateFlow(FlatUiState())
    val flatUiState = _flatUiState.asStateFlow()

    init {
        getFlatBills()
    }


    private fun getFlatBills() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.getFlatBills().collect { flatBills ->
                    _flatUiState.update { it.copy(flatBills = flatBills) }
                }
            }
        }
    }

    fun insertFlatBill(flatBill: FlatBill) {
        val formatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)
        val currentDateTime = LocalDateTime.now().format(formatter)

        flatBill.paymentDate = currentDateTime
        flatBill.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.insertFlatBill(flatBill)
            }
        }
    }

    fun updateFlatBill(flatBill: FlatBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.updateFlatBill(flatBill)
            }
        }
    }

    fun deleteFlatBill(flatBill: FlatBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                firebaseRepository.deleteFlatBill(flatBill)
            }
        }
    }


}