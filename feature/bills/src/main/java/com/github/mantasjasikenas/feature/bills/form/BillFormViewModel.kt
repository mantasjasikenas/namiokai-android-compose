package com.github.mantasjasikenas.feature.bills.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.github.mantasjasikenas.core.common.util.Constants.DATE_TIME_FORMAT
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.core.domain.model.bills.Bill
import com.github.mantasjasikenas.core.domain.repository.BillsRepository
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.feature.bills.navigation.BillFormRoute
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class BillFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    spaceRepository: SpaceRepository,
    private val billsRepository: BillsRepository
) :
    ViewModel() {

    val billFormRoute = savedStateHandle.toRoute<BillFormRoute>()

    val billFormUiState: StateFlow<BillFormUiState> = combine(
        spaceRepository.getCurrentUserSpaces(),
        billFlow()
    ) { spaces, bill ->
        BillFormUiState.Success(
            spaces = spaces,
            initialBill = bill
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BillFormUiState.Loading
    )

    private fun billFlow() = flow {
        if (billFormRoute.billId == null || billFormRoute.billType == null) {
            emit(null)

            return@flow
        }

        val billFlow = billsRepository
            .getBill(billFormRoute.billId, billFormRoute.billType)

        emitAll(billFlow)
    }

    fun insertBill(bill: Bill) {
        bill.date = getCurrentDateTime()
        bill.createdByUid = Firebase.auth.uid ?: ""

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                billsRepository.insertBill(bill)
            }
        }
    }

    fun updateBill(bill: Bill) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                billsRepository.updateBill(bill)
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)

        return current.format(formatter)
    }
}

sealed interface BillFormUiState {
    data object Loading : BillFormUiState
    data class Success(
        val spaces: List<Space> = emptyList(),
        val initialBill: Bill? = null
    ) : BillFormUiState
}