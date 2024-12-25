package com.github.mantasjasikenas.feature.debts.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.debts.DebtsRoute
import kotlinx.serialization.Serializable

@Serializable
data object DebtsRoute

fun NavController.navigateToDebts(
    navOptions: NavOptions? = null,
) {
    navigate(route = DebtsRoute, navOptions)
}

fun NavGraphBuilder.debtsScreen(
    modifier: Modifier = Modifier,
) {
    composable<DebtsRoute> {
        DebtsRoute()
    }
}