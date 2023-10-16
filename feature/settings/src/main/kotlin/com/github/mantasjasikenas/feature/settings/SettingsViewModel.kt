package com.github.mantasjasikenas.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.ToastManager
import com.github.mantasjasikenas.core.database.AccentColor
import com.github.mantasjasikenas.core.domain.repository.AuthRepository
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.UserData
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.repository.AccentColorRepository
import com.github.mantasjasikenas.core.domain.repository.UserDataRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usersRepository: UsersRepository,
    private val accentColorRepository: AccentColorRepository,
    private val userDataRepository: UserDataRepository,
    private val toastManager: ToastManager
) : ViewModel() {

    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map { userData ->
                SettingsUiState.Success(
                    accentColors = emptyList(),
                    userData = userData
                )
            }
            .combine(accentColorRepository.getAllAccentColorsStream()) { uiState, accentColors ->
                uiState.copy(
                    accentColors = accentColors
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState.Loading
            )

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
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

    fun updateThemePreferences(themePreferences: ThemePreferences) {
        viewModelScope.launch {
            userDataRepository.updateThemePreferences(themePreferences)
        }
    }

    fun insertAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                accentColorRepository.insertAccentColor(accentColor)
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

    fun updateAccentColorPinStatus(
        id: Int,
        value: Boolean
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                /*val accentColor = settingsUiState.value.accentColors.firstOrNull {
                    it.id == id
                } ?: return@withContext

                accentColorRepository.updateAccentColor(
                    accentColor.copy(pinned = value)
                )*/

                accentColorRepository.updateAccentColorPinned(
                    id = id,
                    pinned = value
                )
            }
        }
    }

    fun validateDisplayName(
        newDisplayName: String
    ): Boolean {
        if (newDisplayName.isBlank()) {
            toastManager.show("Name cannot be blank")
            return false
        }

        if (settingsUiState.value is SettingsUiState.Success) {
            val userData = (settingsUiState.value as SettingsUiState.Success).userData
            if (newDisplayName == userData.user.displayName) {
                toastManager.show("Name is the same as before")
                return false
            }
        }

//       if (newDisplayName == usersRepository.currentUser.displayName) {
//           toastManager.show("Name is the same as before")
//           return false
//       }
//
//       mainUiState.usersMap.values.forEach {
//           if (it.displayName == newDisplayName) {
//               toastManager.show("Name already taken")
//               return false
//           }
//       }

        return true
    }
}


sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val accentColors: List<AccentColor>,
        val userData: UserData
    ) : SettingsUiState
}
