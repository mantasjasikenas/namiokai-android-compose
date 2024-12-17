package com.github.mantasjasikenas.feature.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.toYearMonthPair
import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.model.filter
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
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
class FuelViewModel @Inject constructor(private val tripBillsRepository: TripBillsRepository) :
    ViewModel() {

    private val _fuelUiState = MutableStateFlow(FuelUiState())
    val uiState = _fuelUiState.asStateFlow()

    val groupedTrips = uiState.map { state ->
        state.filteredTripBills.groupBy {
            it.date.toYearMonthPair()
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyMap()
        )

    init {
        getFuel()
        getDestinations()
    }

    fun onFiltersChanged(filters: List<Filter<TripBill, Any>>) {
        _fuelUiState.update {
            it.copy(
                filters = filters,
                filteredTripBills = it.tripBills.filter(filters)
            )
        }

    }

    private fun getFuel() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripBillsRepository.getTripBills()
                    .collect { fuels ->
                        _fuelUiState.update {
                            val filters = it.filters
                            it.copy(
                                tripBills = fuels,
                                filteredTripBills = fuels.filter(filters)
                            )
                        }
                    }
            }
        }
    }

    private fun getDestinations() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripBillsRepository.getDestinations()
                    .collect { destinations ->
                        _fuelUiState.update { it.copy(destinations = destinations) }
                    }
            }
        }
    }

    fun deleteFuel(tripBill: TripBill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripBillsRepository.deleteTripBill(tripBill)
            }
        }

    }
}

data class FuelUiState(
    val tripBills: List<TripBill> = emptyList(),
    val destinations: List<Destination> = emptyList(),
    val filteredTripBills: List<TripBill> = emptyList(),
    val filters: List<Filter<TripBill, Any>> = emptyList(),
) {
    fun isLoading(): Boolean {
        return tripBills.isEmpty() && filteredTripBills.isEmpty() && filters.isEmpty() && destinations.isEmpty()
    }
}