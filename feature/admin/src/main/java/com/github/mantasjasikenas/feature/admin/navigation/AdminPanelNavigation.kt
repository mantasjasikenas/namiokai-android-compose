package com.github.mantasjasikenas.feature.admin.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.admin.AdminPanelRoute
import kotlinx.serialization.Serializable

@Serializable
data object AdminPanelRoute

fun NavController.navigateToAdminPanel(
    navOptions: NavOptions? = null,
) {
    navigate(route = AdminPanelRoute, navOptions)
}

fun NavGraphBuilder.adminPanelScreen() {
    composable<AdminPanelRoute> {
        AdminPanelRoute()
    }
}