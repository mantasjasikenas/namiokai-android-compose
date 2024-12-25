package com.github.mantasjasikenas.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.settings.SettingsRoute
import com.github.mantasjasikenas.feature.settings.navigation.SettingsRoute
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings(
    navOptions: NavOptions? = null,
) {
    navigate(route = SettingsRoute, navOptions)
}

fun NavGraphBuilder.settingsScreen() {
    composable<SettingsRoute> {
        SettingsRoute()
    }
}