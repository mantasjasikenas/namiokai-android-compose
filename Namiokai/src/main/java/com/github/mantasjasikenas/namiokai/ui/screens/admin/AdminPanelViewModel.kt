package com.github.mantasjasikenas.namiokai.ui.screens.admin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
import com.github.mantasjasikenas.namiokai.data.repository.BILL_IMPORT_FILE_NAME
import com.github.mantasjasikenas.namiokai.data.repository.FUEL_IMPORT_FILE_NAME
import com.github.mantasjasikenas.namiokai.data.repository.USERS_IMPORT_FILE_NAME
import com.github.mantasjasikenas.namiokai.model.User
import com.github.mantasjasikenas.namiokai.utils.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val toastManager: ToastManager,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    fun backupDatabase() {
        toastManager.show("Backup started")

        viewModelScope.launch {
            firebaseRepository.backupCollections()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Backup failed")
            else
                toastManager.show("Backup successful")
        }
    }

    fun clearBills() {
        toastManager.show("Bills clear started")

        viewModelScope.launch {
            firebaseRepository.clearBills()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Bills clear failed")
            else
                toastManager.show("Bills cleared")
        }
    }

    fun clearFuel() {
        toastManager.show("Fuel clear started")

        viewModelScope.launch {
            firebaseRepository.clearFuel()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Fuel clear failed")
            else
                toastManager.show("Fuel cleared")
        }
    }

    fun clearFlatBills() {
        toastManager.show("Flat bills clear started")

        viewModelScope.launch {
            firebaseRepository.clearFlatBills()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Flat bills clear failed")
            else
                toastManager.show("Flat bills cleared")
        }
    }

    fun clearUsers() {
        toastManager.show("Users clear started")

        viewModelScope.launch {
            firebaseRepository.clearUsers()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Users clear failed")
            else
                toastManager.show("Users cleared")
        }
    }

    fun clearBillsAndFuel() {
        toastManager.show("Records clear started")
        viewModelScope.launch {
            firebaseRepository.clearBillsAndFuel()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Records clear failed")
            else
                toastManager.show("Records cleared")
        }
    }

    fun addUser() {
        viewModelScope.launch {
            firebaseRepository.insertUser(
                User(
                    displayName = "TestUser",
                    uid = UUID.randomUUID().toString()
                )
            )
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("User add failed")
            else
                toastManager.show("User added")
        }
    }

    fun importBills() {
        viewModelScope.launch {
            firebaseRepository.loadBillsFromStorage(BILL_IMPORT_FILE_NAME)
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Bills import failed")
            else
                toastManager.show("Bills imported")
        }
    }

    fun importFuel() {
        viewModelScope.launch {
            firebaseRepository.loadFuelFromStorage(FUEL_IMPORT_FILE_NAME)
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Fuel import failed")
            else
                toastManager.show("Fuel imported")
        }
    }

    fun importUsers() {
        viewModelScope.launch {
            firebaseRepository.loadUsersFromStorage(USERS_IMPORT_FILE_NAME)
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Users import failed")
            else
                toastManager.show("Users imported")
        }
    }

}