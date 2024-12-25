package com.github.mantasjasikenas.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.profile.ProfileRoute
import com.github.mantasjasikenas.feature.profile.navigation.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

fun NavController.navigateToProfile(
    navOptions: NavOptions? = null,
) {
    navigate(route = ProfileRoute, navOptions)
}

fun NavGraphBuilder.profileScreen() {
    composable<ProfileRoute> {
        ProfileRoute()
    }
}