package com.github.mantasjasikenas.namiokai.ui.screens.bill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.PurchaseBillsRepository
import com.github.mantasjasikenas.namiokai.model.bills.PurchaseBill
import com.github.mantasjasikenas.namiokai.utils.Constants.DATE_TIME_FORMAT
import com.github.mantasjasikenas.namiokai.utils.Filter
import com.github.mantasjasikenas.namiokai.utils.filterAll
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
class BillViewModel @Inject constructor(private val purchaseBillsRepository: PurchaseBillsRepository) : ViewModel() {

    private val _billUiState = MutableStateFlow(BillUiState())
    val uiState = _billUiState.asStateFlow()

    init {
        getBills()
        //applyFilters()
    }

    private fun getBills() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                purchaseBillsRepository.getPurchaseBills()
                    .collect { bills ->
                        _billUiState.update {
                            val filters = it.appliedFilters.values.toList()

                            it.copy(
                                purchaseBills = bills,
                                filteredPurchaseBills = bills.filterAll(filters)
                            )
                        }
                    }
            }
        }
    }


    fun insertBill(purchaseBill: PurchaseBill) {
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        val currentDateTime = LocalDateTime.now()
            .format(formatter)

        purchaseBill.date = currentDateTime
        purchaseBill.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                purchaseBillsRepository.insertBill(purchaseBill)
            }
        }
    }

    fun updateBill(purchaseBill: PurchaseBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                purchaseBillsRepository.updateBill(purchaseBill)
            }
        }
    }

    fun deleteBill(purchaseBill: PurchaseBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                purchaseBillsRepository.deleteBill(purchaseBill)
            }
        }
    }

    fun addFilter(
        fieldName: String,
        filter: Filter<PurchaseBill>
    ) {
        _billUiState.update {
            val map = it.appliedFilters.toMutableMap()
                .apply {
                    put(
                        fieldName,
                        filter
                    )
                }


            it.copy(
                appliedFilters = map,
                filteredPurchaseBills = it.purchaseBills.filterAll(map.values)
            )
        }
    }

    fun removeFilter(fieldName: String) {
        _billUiState.update {
            val map = it.appliedFilters.toMutableMap()
                .apply { remove(fieldName) }

            it.copy(
                appliedFilters = map,
                filteredPurchaseBills = it.purchaseBills.filterAll(map.values)
            )
        }
    }


    fun resetFilters() {
        _billUiState.update {
            it.copy(
                appliedFilters = emptyMap(),
                filteredPurchaseBills = it.purchaseBills
            )
        }
    }


    /*fun applyFilters(
        filters: Filters<PurchaseBill> = emptyList()
    ) {
        if (filters.isEmpty()) {
            _billUiState.update { it.copy(filteredPurchaseBills = _billUiState.value.purchaseBills) }
            return
        }

        val filteredBills = _billUiState.value.purchaseBills.filterAll(filters)
        _billUiState.update { it.copy(filteredPurchaseBills = filteredBills) }
    }*/


}


