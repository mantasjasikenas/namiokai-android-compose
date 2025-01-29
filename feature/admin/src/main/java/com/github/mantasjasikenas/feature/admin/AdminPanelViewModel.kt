package com.github.mantasjasikenas.feature.admin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.ToastManager
import com.github.mantasjasikenas.core.data.repository.BILL_IMPORT_FILE_NAME
import com.github.mantasjasikenas.core.data.repository.FUEL_IMPORT_FILE_NAME
import com.github.mantasjasikenas.core.data.repository.USERS_IMPORT_FILE_NAME
import com.github.mantasjasikenas.core.domain.model.User
import com.github.mantasjasikenas.core.domain.repository.FlatBillsRepository
import com.github.mantasjasikenas.core.domain.repository.PurchaseBillsRepository
import com.github.mantasjasikenas.core.domain.repository.SpaceRepository
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val toastManager: ToastManager,
    private val purchaseBillsRepository: PurchaseBillsRepository,
    private val tripBillsRepository: TripBillsRepository,
    private val flatBillsRepository: FlatBillsRepository,
    private val usersRepository: UsersRepository,
    private val spaceRepository: SpaceRepository
) : ViewModel() {

    fun backupDatabase() {
        toastManager.show(R.string.backup_started)

        viewModelScope.launch {
            val currentDateTime = LocalDateTime.now()
                .toString()

            purchaseBillsRepository.backupCollection(currentDateTime)
            tripBillsRepository.backupCollection(currentDateTime)
            flatBillsRepository.backupCollection(currentDateTime)
            usersRepository.backupCollection(currentDateTime)
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.backup_failed)
                else
                    toastManager.show(R.string.backup_successful)
            }
    }

    fun clearBills() {
        toastManager.show(R.string.records_clear_started)
        viewModelScope.launch {
            purchaseBillsRepository.clearPurchaseBills()
            tripBillsRepository.clearTripBills()
            flatBillsRepository.clearFlatBills()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.records_clear_failed)
                else
                    toastManager.show(R.string.records_cleared)
            }
    }

    fun clearPurchaseBills() {
        toastManager.show(R.string.purchase_bills_clear_started)

        viewModelScope.launch {
            purchaseBillsRepository.clearPurchaseBills()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.purchase_bills_clear_failed)
                else
                    toastManager.show(R.string.purchase_bills_cleared)
            }
    }

    fun clearFuel() {
        toastManager.show(R.string.fuel_clear_started)

        viewModelScope.launch {
            tripBillsRepository.clearTripBills()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.fuel_clear_failed)
                else
                    toastManager.show(R.string.fuel_cleared)
            }
    }

    fun clearFlatBills() {
        toastManager.show(R.string.flat_bills_clear_started)

        viewModelScope.launch {
            flatBillsRepository.clearFlatBills()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.flat_bills_clear_failed)
                else
                    toastManager.show(R.string.flat_bills_cleared)
            }
    }

    fun clearUsers() {
        toastManager.show(R.string.users_clear_started)

        viewModelScope.launch {
            usersRepository.clearUsers()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.users_clear_failed)
                else
                    toastManager.show(R.string.users_cleared)
            }
    }

    fun addUser() {
        viewModelScope.launch {
            usersRepository.insertUser(
                User(
                    displayName = "TestUser",
                    uid = UUID.randomUUID()
                        .toString()
                )
            )
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.user_add_failed)
                else
                    toastManager.show(R.string.user_added)
            }
    }

    fun importBills() {
        viewModelScope.launch {
            purchaseBillsRepository.loadPurchaseBillsFromStorage(BILL_IMPORT_FILE_NAME)
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.bills_import_failed)
                else
                    toastManager.show(R.string.bills_imported)
            }
    }

    fun importFuel() {
        viewModelScope.launch {
            tripBillsRepository.loadTripsFromStorage(FUEL_IMPORT_FILE_NAME)
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.fuel_import_failed)
                else
                    toastManager.show(R.string.fuel_imported)
            }
    }

    fun importUsers() {
        viewModelScope.launch {
            usersRepository.loadUsersFromStorage(USERS_IMPORT_FILE_NAME)
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show(R.string.users_import_failed)
                else
                    toastManager.show(R.string.users_imported)
            }
    }

    suspend fun assignSpaceToBills(spaceId: String) {
        spaceRepository.getSpace(spaceId).firstOrNull() ?: run {
            toastManager.show(R.string.space_not_found)
            return
        }

        try {
            purchaseBillsRepository.updatePurchaseBills { it.copy(spaceId = spaceId) }
            tripBillsRepository.updateTripBills { it.copy(spaceId = spaceId) }
            flatBillsRepository.updateFlatBills { it.copy(spaceId = spaceId) }
        } catch (e: Exception) {
            toastManager.show(R.string.space_assign_failed)
        } finally {
            toastManager.show(R.string.space_assigned)
        }
    }
}