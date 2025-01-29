package com.github.mantasjasikenas.feature.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.localization.LocalizationManager
import com.github.mantasjasikenas.core.common.util.ToastManager
import com.github.mantasjasikenas.core.database.AccentColor
import com.github.mantasjasikenas.core.domain.model.Response
import com.github.mantasjasikenas.core.domain.model.UserData
import com.github.mantasjasikenas.core.domain.model.theme.ThemePreferences
import com.github.mantasjasikenas.core.domain.repository.AccentColorRepository
import com.github.mantasjasikenas.core.domain.repository.AuthRepository
import com.github.mantasjasikenas.core.domain.repository.UserDataRepository
import com.github.mantasjasikenas.core.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val toastManager: ToastManager,
    private val localizationManager: LocalizationManager,
    @ApplicationContext private val context: Context
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

    val currentLanguageIso: String
        get() = localizationManager.getLanguageCode()

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun addImageToStorage(imageUri: Uri) = viewModelScope.launch {
        toastManager.show(context.getString(R.string.uploading_image))

        when (val response = usersRepository.addImageToFirebaseStorage(imageUri)) {
            is Response.Success -> {
                response.data?.let { uri ->
                    usersRepository.changeCurrentUserImageUrlInFirestore(uri)
                }
                toastManager.show(context.getString(R.string.image_uploaded_successfully))
            }

            is Response.Failure -> {
                println(response.e)
                toastManager.show(context.getString(R.string.image_upload_failed))
            }

            else -> {}
        }
    }

    fun updateDisplayName(newName: String) = viewModelScope.launch {
        toastManager.show(context.getString(R.string.updating_name))

        val response = usersRepository.changeCurrentUserNameInFirestore(newName)

        when (response) {
            is Response.Success -> {
                toastManager.show(context.getString(R.string.name_changed_successfully))
            }

            else -> {
                toastManager.show(context.getString(R.string.name_change_failed))
            }
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
            toastManager.show(context.getString(R.string.name_cannot_be_blank))
            return false
        }

        if (settingsUiState.value is SettingsUiState.Success) {
            val userData = (settingsUiState.value as SettingsUiState.Success).userData
            if (newDisplayName == userData.user.displayName) {
                toastManager.show(context.getString(R.string.name_is_the_same_as_before))
                return false
            }
        }

        return true
    }

    fun updateLanguage(languageIso: String) {
        localizationManager.applyLanguage(languageIso)
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val accentColors: List<AccentColor>,
        val userData: UserData
    ) : SettingsUiState
}
