@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.mantasjasikenas.feature.bills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.toYearMonthPair
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.filter
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PurchaseBillViewModel @Inject constructor(
    private val purchaseBillsRepository: PurchaseBillsRepository,
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    private val _billUiState: MutableStateFlow<BillUiState> = MutableStateFlow(BillUiState())
    val billUiState = _billUiState.asStateFlow()

    val groupedBills = billUiState.map { state ->
        state.filteredPurchaseBills
            .groupBy {
                it.date.toYearMonthPair()
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    init {
        observePurchaseBills()
    }

    private fun observePurchaseBills() {
        viewModelScope.launch {
            _billUiState.update { it.copy(isLoading = true) }

            spaceRepository.getCurrentUserSpaces()
                .map { spaces ->
                    _billUiState.update { it.copy(spaces = spaces) }
                    spaces.map { it.spaceId }
                }
                .flatMapLatest { spaceIds ->
                    purchaseBillsRepository.getPurchaseBills(spaceIds = spaceIds)
                        .catch {
                            _billUiState.update { it.copy(isLoading = false) }
                            emit(emptyList())
                        }
                }
                .collect { bills ->
                    _billUiState.update { state ->
                        state.copy(
                            purchaseBills = bills,
                            filteredPurchaseBills = bills.filter(state.filters),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onFiltersChanged(filters: List<Filter<PurchaseBill, Any>>) {
        _billUiState.update {
            it.copy(
                filters = filters,
                filteredPurchaseBills = it.purchaseBills.filter(filters)
            )
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
    val isLoading: Boolean = true,
    val purchaseBills: List<PurchaseBill> = emptyList(),
    val filteredPurchaseBills: List<PurchaseBill> = emptyList(),
    val filters: List<Filter<PurchaseBill, Any>> = emptyList(),
    val spaces: List<Space> = emptyList()
)