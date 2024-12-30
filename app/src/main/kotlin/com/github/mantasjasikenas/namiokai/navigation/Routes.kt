package com.github.mantasjasikenas.namiokai.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Workspaces
import androidx.compose.material.icons.rounded.Cottage
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Workspaces
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.mantasjasikenas.feature.bills.navigation.PurchaseBillRoute
import com.github.mantasjasikenas.feature.debts.navigation.DebtsRoute
import com.github.mantasjasikenas.feature.flat.navigation.FlatRoute
import com.github.mantasjasikenas.feature.home.navigation.HomeRoute
import com.github.mantasjasikenas.feature.space.navigation.SpaceRoute
import com.github.mantasjasikenas.feature.trips.navigation.TripBillRoute
import com.github.mantasjasikenas.namiokai.R
import kotlinx.serialization.Serializable


sealed interface Route {
    @Serializable
    data object RootGraph : Route

    @Serializable
    data object AuthGraph : Route

    @Serializable
    data object HomeGraph : Route

    /*    companion object {
            val routes = listOf(
                HomeRoute,
                DebtsRoute,
                PurchaseBillRoute,
                TripBillRoute,
                FlatRoute,
                FlatBillListRoute,
                SettingsRoute,
                NotificationsRoute,
                LoginRoute,
                TestRoute,
                AdminPanelRoute,
                ProfileRoute
            )
        }*/
}

sealed class TopLevelRoute(
    val route: Any,
    @StringRes val titleResourceId: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector = unselectedIcon
) {
    data object Home : TopLevelRoute(
        HomeRoute,
        R.string.home_menu_label,
        Icons.Outlined.Home,
        Icons.Rounded.Home
    )

    data object Debts : TopLevelRoute(
        DebtsRoute,
        R.string.debts_name,
        Icons.Outlined.Payments,
        Icons.Rounded.Payments
    )

    data object Bills : TopLevelRoute(
        PurchaseBillRoute,
        R.string.bill_name,
        Icons.Outlined.ShoppingBag,
        Icons.Rounded.ShoppingBag
    )

    data object Trips : TopLevelRoute(
        TripBillRoute,
        R.string.trips_name,
        Icons.Outlined.LocalGasStation,
        Icons.Rounded.LocalGasStation
    )

    data object Flat : TopLevelRoute(
        FlatRoute,
        R.string.flat_label,
        Icons.Outlined.Cottage,
        Icons.Rounded.Cottage
    )

    data object Space : TopLevelRoute(
        SpaceRoute,
        R.string.space_label,
        Icons.Outlined.Workspaces,
        Icons.Rounded.Workspaces
    )

    companion object {
        val routes = listOf(Home, Debts, Bills, Trips, Flat, Space)
    }
}