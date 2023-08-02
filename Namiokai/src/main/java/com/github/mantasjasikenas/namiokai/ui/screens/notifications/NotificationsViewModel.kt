package com.github.mantasjasikenas.namiokai.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.namiokai.data.Notification
import com.github.mantasjasikenas.namiokai.data.NotificationsRepository
import com.github.mantasjasikenas.namiokai.utils.Constants.TIMEOUT_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class NotificationsViewModel @Inject constructor(
    notificationsRepository: NotificationsRepository
) : ViewModel() {
    val notificationsUiState: StateFlow<NotificationsUiState> =
        notificationsRepository.getAllNotificationsStream()
            .map { NotificationsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = NotificationsUiState()
            )
}

data class NotificationsUiState(val notificationList: List<Notification> = listOf())