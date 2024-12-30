package com.github.mantasjasikenas.feature.space.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.SpaceFormArgs
import com.github.mantasjasikenas.feature.space.SpaceRoute
import kotlinx.serialization.Serializable

@Serializable
data object SpaceRoute

fun NavController.navigateToSpace(
    navOptions: NavOptions? = null,
) {
    navigate(route = SpaceRoute, navOptions)
}

fun NavGraphBuilder.spaceScreen(
    sharedState: SharedState,
    onNavigateToCreateSpace: (SpaceFormArgs) -> Unit,
) {
    composable<SpaceRoute> {
        SpaceRoute(
            sharedState = sharedState,
            onNavigateToCreateSpace = onNavigateToCreateSpace,
        )
    }
}