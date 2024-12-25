package com.github.mantasjasikenas.feature.trips.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.bills.BillFormArgs
import com.github.mantasjasikenas.feature.trips.TripBillRoute
import kotlinx.serialization.Serializable

@Serializable
data object TripBillRoute

fun NavController.navigateToTripBill(
    navOptions: NavOptions? = null,
) {
    navigate(route = TripBillRoute, navOptions)
}

fun NavGraphBuilder.tripBillScreen(
    sharedState: SharedState,
    onNavigateToCreateBill: (BillFormArgs) -> Unit,
) {
    composable<TripBillRoute> {
        TripBillRoute(
            sharedState = sharedState,
            onNavigateToCreateBill = onNavigateToCreateBill,
        )
    }
}