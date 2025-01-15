package com.github.mantasjasikenas.feature.space.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.feature.space.form.SpaceFormRoute
import kotlinx.serialization.Serializable

@Serializable
data class SpaceFormRoute(
    val spaceId: String? = null,
)

fun NavController.navigateToSpaceForm(
    spaceId: String? = null,
    navOptions: NavOptions? = null,
) {
    navigate(
        route = SpaceFormRoute(
            spaceId = spaceId
        ), navOptions
    )
}

fun NavGraphBuilder.spaceFormScreen(
    sharedState: SharedState,
    onNavigateUp: () -> Unit,
    onNavigateToInviteUsers: () -> Unit
) {
    composable<SpaceFormRoute> { entry ->
        val invitedUsers = entry.savedStateHandle.get<List<String>>("selectedUsers")

        SpaceFormRoute(
            sharedState = sharedState,
            onNavigateUp = onNavigateUp,
            invitedUsers = invitedUsers,
            onNavigateToInviteUsers = onNavigateToInviteUsers
        )
    }
}