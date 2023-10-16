package com.github.mantasjasikenas.feature.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.Constants.DATE_TIME_FORMAT
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.filter
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
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
class BillViewModel @Inject constructor(private val purchaseBillsRepository: PurchaseBillsRepository) : ViewModel() {

    private val _billUiState: MutableStateFlow<BillUiState> = MutableStateFlow(BillUiState())
    val billUiState = _billUiState.asStateFlow()


    init {
        getBills()
    }

    fun onFiltersChanged(filters: List<Filter<PurchaseBill, Any>>) {
        _billUiState.update {
            it.copy(
                filters = filters,
                filteredPurchaseBills = it.purchaseBills.filter(filters)
            )
        }
    }

    private fun getBills() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                purchaseBillsRepository.getPurchaseBills()
                    .collect { bills ->
                        _billUiState.update {
                            val filters = it.filters

                            it.copy(
                                purchaseBills = bills,
                                filteredPurchaseBills = bills.filter(filters)
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


}

data class BillUiState(
    val purchaseBills: List<PurchaseBill> = emptyList(),
    val filteredPurchaseBills: List<PurchaseBill> = emptyList(),
    val filters: List<Filter<PurchaseBill, Any>> = emptyList(),
) {
    fun isLoading(): Boolean {
        return purchaseBills.isEmpty() && filteredPurchaseBills.isEmpty() && filters.isEmpty()
    }
}

/*sealed interface BillUiState {
    data object Loading : BillUiState
    data class Success(
        val purchaseBills: List<PurchaseBill> = emptyList(),
        val filteredPurchaseBills: List<PurchaseBill> = emptyList(),
        val filters: List<Filter<PurchaseBill, Any>> = emptyList(),
    ) : BillUiState
}*/


