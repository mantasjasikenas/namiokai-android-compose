package com.github.mantasjasikenas.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.Space
import com.github.mantasjasikenas.feature.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavController.navigateToHome(
    navOptions: NavOptions? = null,
) {
    navigate(route = HomeRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onNavigateToSpaceForm: (Space?) -> Unit,
    onNavigateToSpaceScreen: () -> Unit,
) {
    composable<HomeRoute> {
        HomeRoute(
            onNavigateToSpace = onNavigateToSpaceForm,
            onNavigateToSpaceScreen = onNavigateToSpaceScreen,
        )
    }
}