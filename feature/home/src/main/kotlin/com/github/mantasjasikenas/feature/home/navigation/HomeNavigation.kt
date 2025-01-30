package com.github.mantasjasikenas.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.space.Space
import com.github.mantasjasikenas.feature.home.HomeRoute
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

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