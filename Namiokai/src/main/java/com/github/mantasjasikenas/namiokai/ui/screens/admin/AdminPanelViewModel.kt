package com.github.mantasjasikenas.namiokai.ui.screens.admin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.FirebaseRepository
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
        viewModelScope.launch {
            firebaseRepository.clearAndBackupCollections()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Backup failed")
        }
        toastManager.show("Backup started")
    }

    fun clearBills() {
        viewModelScope.launch {
            firebaseRepository.clearBills()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Bills clear failed")
        }
        toastManager.show("Bills cleared")
    }

    fun clearFuel() {
        viewModelScope.launch {
            firebaseRepository.clearFuel()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Fuel clear failed")
        }
        toastManager.show("Fuel cleared")
    }

    fun clearUsers() {
        viewModelScope.launch {
            firebaseRepository.clearUsers()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Users clear failed")
        }
        toastManager.show("Users cleared")
    }

    fun addUser() {
        viewModelScope.launch {
            firebaseRepository.insertUser(
                User(
                    displayName = "Test User",
                    uid = UUID.randomUUID().toString()
                )
            )
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("User add failed")
        }
        toastManager.show("User added")
    }
}