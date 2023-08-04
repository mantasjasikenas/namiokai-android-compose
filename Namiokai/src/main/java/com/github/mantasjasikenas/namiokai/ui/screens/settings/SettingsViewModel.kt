package com.github.mantasjasikenas.namiokai.ui.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.AccentColor
import com.github.mantasjasikenas.namiokai.data.AccentColorRepository
import com.github.mantasjasikenas.namiokai.data.UsersRepository
import com.github.mantasjasikenas.namiokai.model.Response
import com.github.mantasjasikenas.namiokai.presentation.sign_in.GoogleAuthUiClient
import com.github.mantasjasikenas.namiokai.ui.main.MainUiState
import com.github.mantasjasikenas.namiokai.utils.ToastManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val usersRepository: UsersRepository,
    private val accentColorRepository: AccentColorRepository,
    private val toastManager: ToastManager
) : ViewModel() {

    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState = _settingsUiState.asStateFlow()

    init {
        getCustomColors()
    }

    private fun getCustomColors() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accentColorRepository.getAllAccentColorsStream()
                    .collect { colors ->
                        _settingsUiState.update {
                            it.copy(
                                accentColors = colors
                            )
                        }
                    }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            googleAuthUiClient.signOut()
        }
    }

    fun addImageToStorage(imageUri: Uri) = viewModelScope.launch {
        when (val addImageToStorageResponse =
            usersRepository.addImageToFirebaseStorage(imageUri)) {
            is Response.Success -> {
                addImageToStorageResponse.data?.let { uri ->
                    usersRepository.changeCurrentUserImageUrlInFirestore(uri)
                }
                toastManager.show("Image uploaded successfully")
            }

            is Response.Failure -> {
                println(addImageToStorageResponse.e)
                toastManager.show("Image upload failed")
            }

            else -> {}
        }
    }

    fun updateDisplayName(newName: String) = viewModelScope.launch {
        val response = usersRepository.changeCurrentUserNameInFirestore(newName)
        if (response is Response.Success) {
            toastManager.show("Name changed successfully")
        }
        else {
            toastManager.show("Name change failed")
        }
    }

    fun validateDisplayName(
        mainUiState: MainUiState,
        name: String
    ): Boolean {
        if (name.isBlank()) {
            toastManager.show("Name cannot be blank")
            return false
        }

        if (name == mainUiState.currentUser.displayName) {
            toastManager.show("Name is the same as before")
            return false
        }

        mainUiState.usersMap.values.forEach {
            if (it.displayName == name) {
                toastManager.show("Name already taken")
                return false
            }
        }

        return true
    }

    fun insertAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accentColorRepository.insertAccentColor(accentColor)
            }
        }
    }

    fun deleteAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accentColorRepository.deleteAccentColor(accentColor)
            }
        }
    }

    fun clearUnpinnedAccentColors() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accentColorRepository.deleteUnpinnedAccentColors()
            }
        }
    }

    fun updateAccentColorPin(
        id: Int,
        value: Boolean
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val accentColor = _settingsUiState.value.accentColors.firstOrNull {
                    it.id == id
                } ?: return@withContext

                accentColorRepository.updateAccentColor(
                    accentColor.copy(pinned = value)
                )
            }
        }
    }
}