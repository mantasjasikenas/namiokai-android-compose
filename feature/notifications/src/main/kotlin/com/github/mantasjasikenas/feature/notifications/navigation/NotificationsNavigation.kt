package com.github.mantasjasikenas.feature.notifications.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.notifications.NotificationsRoute
import kotlinx.serialization.Serializable

@Serializable
data object NotificationsRoute

fun NavGraphBuilder.notificationsScreen() {
    composable<NotificationsRoute> {
        NotificationsRoute()
    }
}