package com.github.mantasjasikenas.feature.bills.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.Constants.DATE_TIME_FORMAT
import com.github.mantasjasikenas.core.domain.model.Destination
import com.github.mantasjasikenas.core.domain.model.bills.FlatBill
import com.github.mantasjasikenas.core.domain.model.bills.PurchaseBill
import com.github.mantasjasikenas.core.domain.model.bills.TripBill
import com.github.mantasjasikenas.core.domain.repository.FlatBillsRepository
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CreateBillViewModel @Inject constructor(
    private val purchaseBillsRepository: PurchaseBillsRepository,
    private val tripBillsRepository: TripBillsRepository,
    private val flatBillsRepository: FlatBillsRepository
) :
    ViewModel() {

    val createBillUiState: StateFlow<CreateBillUiState> =
        tripBillsRepository.getDestinations()
            .map { destinations ->
                CreateBillUiState.Success(destinations = destinations)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CreateBillUiState.Loading
            )


    fun insertBill(purchaseBill: PurchaseBill) {
        purchaseBill.date = getCurrentDateTime()
        purchaseBill.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                purchaseBillsRepository.insertBill(purchaseBill)
            }
        }
    }

    fun insertFuel(tripBill: TripBill) {
        tripBill.date = getCurrentDateTime()
        tripBill.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripBillsRepository.insertFuel(tripBill)
            }
        }
    }

    fun insertFlatBill(flatBill: FlatBill) {
        flatBill.date = getCurrentDateTime()
        flatBill.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                flatBillsRepository.insertFlatBill(flatBill)
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)

        return current.format(formatter)
    }
}

sealed interface CreateBillUiState {
    data object Loading : CreateBillUiState
    data class Success(
        val destinations: List<Destination> = emptyList(),
    ) : CreateBillUiState
}


