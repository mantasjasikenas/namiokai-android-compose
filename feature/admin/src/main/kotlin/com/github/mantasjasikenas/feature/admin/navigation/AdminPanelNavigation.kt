package com.github.mantasjasikenas.feature.admin.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.admin.AdminPanelRoute
import kotlinx.serialization.Serializable

@Serializable
data object AdminPanelRoute

fun NavGraphBuilder.adminPanelScreen() {
    composable<AdminPanelRoute> {
        AdminPanelRoute()
    }
}