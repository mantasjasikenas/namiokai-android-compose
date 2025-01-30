package com.github.mantasjasikenas.feature.space.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.SpaceFormArgs
import com.github.mantasjasikenas.feature.space.SpaceRoute
import kotlinx.serialization.Serializable

@Serializable
data object SpaceRoute

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