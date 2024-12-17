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
import com.github.mantasjasikenas.core.domain.repository.TripBillsRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val usersRepository: UsersRepository
) : ViewModel() {

    fun backupDatabase() {
        toastManager.show("Backup started")

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
                    toastManager.show("Backup failed")
                else
                    toastManager.show("Backup successful")
            }
    }

    fun clearBills() {
        toastManager.show("Records clear started")
        viewModelScope.launch {
            purchaseBillsRepository.clearPurchaseBills()
            tripBillsRepository.clearTripBills()
            flatBillsRepository.clearFlatBills()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show("Records clear failed")
                else
                    toastManager.show("Records cleared")
            }
    }

    fun clearPurchaseBills() {
        toastManager.show("Purchase bills clear started")

        viewModelScope.launch {
            purchaseBillsRepository.clearPurchaseBills()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show("Purchase bills clear failed")
                else
                    toastManager.show("Purchase bills cleared")
            }
    }

    fun clearFuel() {
        toastManager.show("Fuel clear started")

        viewModelScope.launch {
            tripBillsRepository.clearTripBills()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show("Fuel clear failed")
                else
                    toastManager.show("Fuel cleared")
            }
    }

    fun clearFlatBills() {
        toastManager.show("Flat bills clear started")

        viewModelScope.launch {
            flatBillsRepository.clearFlatBills()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show("Flat bills clear failed")
                else
                    toastManager.show("Flat bills cleared")
            }
    }

    fun clearUsers() {
        toastManager.show("Users clear started")

        viewModelScope.launch {
            usersRepository.clearUsers()
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show("Users clear failed")
                else
                    toastManager.show("Users cleared")
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
                    toastManager.show("User add failed")
                else
                    toastManager.show("User added")
            }
    }

    fun importBills() {
        viewModelScope.launch {
            purchaseBillsRepository.loadPurchaseBillsFromStorage(BILL_IMPORT_FILE_NAME)
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show("Bills import failed")
                else
                    toastManager.show("Bills imported")
            }
    }

    fun importFuel() {
        viewModelScope.launch {
            tripBillsRepository.loadTripsFromStorage(FUEL_IMPORT_FILE_NAME)
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show("Fuel import failed")
                else
                    toastManager.show("Fuel imported")
            }
    }

    fun importUsers() {
        viewModelScope.launch {
            usersRepository.loadUsersFromStorage(USERS_IMPORT_FILE_NAME)
        }
            .invokeOnCompletion { throwable ->
                if (throwable != null)
                    toastManager.show("Users import failed")
                else
                    toastManager.show("Users imported")
            }
    }

}