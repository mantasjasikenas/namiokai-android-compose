@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.mantasjasikenas.feature.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.toYearMonthPair
import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.Filter
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.model.filter
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FuelViewModel @Inject constructor(
    private val tripBillsRepository: TripBillsRepository,
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    private val _fuelUiState = MutableStateFlow(FuelUiState())
    val uiState = _fuelUiState.asStateFlow()

    val groupedTrips = uiState.map { state ->
        state.filteredTripBills.groupBy {
            it.date.toYearMonthPair()
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    init {
        observeTripBills()
    }

    fun onFiltersChanged(filters: List<Filter<TripBill, Any>>) {
        _fuelUiState.update {
            it.copy(
                filters = filters,
                filteredTripBills = it.tripBills.filter(filters)
            )
        }

    }

    private fun observeTripBills() {
        viewModelScope.launch {
            _fuelUiState.update { it.copy(isLoading = true) }

            spaceRepository.getCurrentUserSpaces()
                .map { spaces -> spaces.map { it.spaceId } }
                .flatMapLatest { spaceIds ->
                    tripBillsRepository.getTripBills(spaceIds = spaceIds)
                        .catch {

                            println("Error: $it")
                            _fuelUiState.update { it.copy(isLoading = false) }
                            emit(emptyList())
                        }
                }
                .combine(tripBillsRepository.getDestinations()) { bills, destinations ->
                    bills to destinations
                }
                .collect { (bills, destinations) ->
                    _fuelUiState.update { state ->
                        state.copy(
                            tripBills = bills,
                            filteredTripBills = bills.filter(state.filters),
                            destinations = destinations,
                            isLoading = false
                        )
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
    val isLoading: Boolean = true,
    val tripBills: List<TripBill> = emptyList(),
    val destinations: List<Destination> = emptyList(),
    val filteredTripBills: List<TripBill> = emptyList(),
    val filters: List<Filter<TripBill, Any>> = emptyList(),
)