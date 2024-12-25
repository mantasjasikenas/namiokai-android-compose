package com.github.mantasjasikenas.feature.notifications.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.notifications.NotificationsRoute
import kotlinx.serialization.Serializable

@Serializable
data object NotificationsRoute

fun NavController.navigateToNotifications(
    navOptions: NavOptions? = null,
) {
    navigate(route = NotificationsRoute, navOptions)
}

fun NavGraphBuilder.notificationsScreen() {
    composable<NotificationsRoute> {
        NotificationsRoute()
    }
}