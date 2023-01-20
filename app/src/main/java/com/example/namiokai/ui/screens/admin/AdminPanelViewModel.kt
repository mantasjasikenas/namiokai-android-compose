package com.example.namiokai.ui.screens.admin


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.namiokai.data.FirebaseRepository
import com.example.namiokai.utils.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPanelViewModel @Inject constructor(
    private val toastManager: ToastManager,
    private val firebaseRepository: FirebaseRepository
) :
    ViewModel() {

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

    fun clearUser() {
        viewModelScope.launch {
            firebaseRepository.clearUsers()
        }.invokeOnCompletion { throwable ->
            if (throwable != null)
                toastManager.show("Users clear failed")
        }

        toastManager.show("Users cleared")

    }
}