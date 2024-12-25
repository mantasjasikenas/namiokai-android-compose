package com.github.mantasjasikenas.feature.flat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.feature.flat.FlatRoute
import kotlinx.serialization.Serializable

@Serializable
data object FlatRoute

fun NavController.navigateToFlat(
    navOptions: NavOptions? = null,
) {
    navigate(route = FlatRoute, navOptions)
}

fun NavGraphBuilder.flatScreen(
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
    onNavigateToFlatBill: () -> Unit
) {
    composable<FlatRoute> {
        FlatRoute(
            sharedState = sharedState,
            onNavigateToCreateBill = onNavigateToCreateBill,
            onNavigateToFlatBill = onNavigateToFlatBill
        )
    }
}