package com.github.mantasjasikenas.feature.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.toYearMonthPair
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.filter
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PurchaseBillViewModel @Inject constructor(private val purchaseBillsRepository: PurchaseBillsRepository) :
    ViewModel() {

    private val _billUiState: MutableStateFlow<BillUiState> = MutableStateFlow(BillUiState())
    val billUiState = _billUiState.asStateFlow()

    val groupedBills = billUiState.map { state ->
        state.filteredPurchaseBills.groupBy {
            it.date.toYearMonthPair()
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyMap()
        )

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

    fun deleteBill(purchaseBill: PurchaseBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                purchaseBillsRepository.deletePurchaseBill(purchaseBill)
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


