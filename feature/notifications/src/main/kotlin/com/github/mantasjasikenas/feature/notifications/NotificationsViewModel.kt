package com.github.mantasjasikenas.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mantasjasikenas.core.common.util.Constants.TIMEOUT_MILLIS
import com.github.mantasjasikenas.core.database.Notification
import com.github.mantasjasikenas.core.domain.repository.NotificationsRepository
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