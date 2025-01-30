package com.github.mantasjasikenas.feature.search_users.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.search_users.SearchUsersRoute
import kotlinx.serialization.Serializable

@Serializable
data object SearchUsersRoute

fun NavGraphBuilder.searchUsersScreen(
    onNavigateBack: (users: List<String>) -> Unit,
) {
    composable<SearchUsersRoute> {
        SearchUsersRoute(
            onNavigateBack = onNavigateBack
        )
    }
}