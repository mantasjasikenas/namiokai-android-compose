package com.github.mantasjasikenas.feature.flat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.filter
import com.github.mantasjasikenas.core.domain.repository.FlatBillsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class FlatViewModel @Inject constructor(private val flatBillsRepository: FlatBillsRepository) :
    ViewModel() {

    private val _flatUiState = MutableStateFlow(FlatUiState())
    val flatUiState = _flatUiState.asStateFlow()

    init {
        getFlatBills()
    }

    fun onFiltersChanged(filters: List<Filter<FlatBill, Any>>) {
        _flatUiState.update {
            it.copy(
                filters = filters,
                filteredFlatBills = it.flatBills.filter(filters)
            )
        }

    }

    private fun getFlatBills() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                flatBillsRepository.getFlatBills()
                    .collect { flatBills ->
                        _flatUiState.update {
                            val filters = it.filters
                            it.copy(
                                flatBills = flatBills,
                                filteredFlatBills = flatBills.filter(filters),
                                electricitySummary = calculateElectricityStats(flatBills)
                            )
                        }
                    }
            }
        }
    }

    fun deleteFlatBill(flatBill: FlatBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                flatBillsRepository.deleteFlatBill(flatBill)
            }
        }
    }

    private fun calculateElectricityStats(bills: List<FlatBill>): ElectricitySummary {
        val bills = bills.reversed()

        val electricityDifference = bills.zipWithNext { previous, current ->
            if (current.taxes == null || previous.taxes == null) {
                return@zipWithNext null
            }

            val difference = ((current.taxes?.electricity ?: 0.0) - (previous.taxes?.electricity
                ?: 0.0)).absoluteValue

            if (difference > 0.0) {
                val startDate = if (previous.date < current.date) previous.date else current.date
                val endDate = if (previous.date < current.date) current.date else previous.date

                BillDifference(startDate, endDate, difference)
            } else {
                null
            }
        }.filterNotNull()

        val (averageDifference, minDifference, maxDifference) = electricityDifference.fold(
            Triple(
                0.0,
                Double.MAX_VALUE,
                Double.MIN_VALUE
            )
        ) { acc, data ->
            val difference = data.difference

            Triple(
                acc.first + difference,
                minOf(acc.second, difference),
                maxOf(acc.third, difference)
            )
        }.let { (sum, min, max) ->
            Triple(sum / electricityDifference.size, min, max)
        }


        return ElectricitySummary(
            averageDifference = averageDifference,
            minDifference = minDifference,
            maxDifference = maxDifference,
            electricityDifference = electricityDifference
        )
    }
}

data class FlatUiState(
    val flatBills: List<FlatBill> = emptyList(),
    val filteredFlatBills: List<FlatBill> = emptyList(),
    val filters: List<Filter<FlatBill, Any>> = emptyList(),
    val electricitySummary: ElectricitySummary? = null
) {
    fun isLoading(): Boolean {
        return flatBills.isEmpty()
    }
}

data class ElectricitySummary(
    val averageDifference: Double,
    val minDifference: Double,
    val maxDifference: Double,
    val electricityDifference: List<BillDifference>
)

data class BillDifference(
    val firstBillDate: String,
    val secondBillDate: String,
    val difference: Double
)