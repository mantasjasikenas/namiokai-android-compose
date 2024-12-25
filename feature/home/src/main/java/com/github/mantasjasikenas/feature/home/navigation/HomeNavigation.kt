package com.github.mantasjasikenas.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.home.HomeRoute
import com.github.mantasjasikenas.feature.home.navigation.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(
    navOptions: NavOptions? = null,
) {
    navigate(route = HomeRoute, navOptions)
}

fun NavGraphBuilder.homeScreen() {
    composable<HomeRoute> {
        HomeRoute()
    }
}