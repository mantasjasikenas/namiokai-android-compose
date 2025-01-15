package com.github.mantasjasikenas.feature.search_users.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.search_users.SearchUsersRoute
import kotlinx.serialization.Serializable

@Serializable
data object SearchUsersRoute

fun NavController.navigateToSearchUsers(
    navOptions: NavOptions? = null,
) {
    navigate(route = SearchUsersRoute, navOptions)
}

fun NavGraphBuilder.searchUsersScreen(
    onNavigateBack: (users: List<String>) -> Unit,
) {
    composable<SearchUsersRoute> {
        SearchUsersRoute(
            onNavigateBack = onNavigateBack
        )
    }
}