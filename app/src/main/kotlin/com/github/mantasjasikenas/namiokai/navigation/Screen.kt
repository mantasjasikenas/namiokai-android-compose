package com.github.mantasjasikenas.namiokai.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import com.github.mantasjasikenas.core.domain.model.bills.BillFormRoute
import com.github.mantasjasikenas.namiokai.R

sealed class Screen(
    val route: String,
    @StringRes val titleResourceId: Int,
    val imageVector: ImageVector = Icons.Outlined.BrokenImage
) {
    data object Home : Screen(
        "home",
        R.string.home_menu_label,
        Icons.Outlined.Home
    )

    data object Debts : Screen(
        "debt",
        R.string.debts_name,
        Icons.Outlined.Payments
    )

    data object Trips : Screen(
        "trips",
        R.string.trips_name,
        Icons.Outlined.LocalGasStation
    )

    data object Bill : Screen(
        "bill",
        R.string.bill_name,
        Icons.Outlined.ShoppingBag
    )

    data object Settings : Screen(
        "settings",
        R.string.settings_name,
        Icons.Outlined.Settings
    )

    data object Notifications : Screen(
        "notifications",
        R.string.notifications_name,
        Icons.Outlined.Notifications
    )

    data object Login : Screen(
        "login",
        R.string.login_name,
        Icons.Outlined.Sync
    )

    data object Test : Screen(
        "test",
        R.string.test_name,
        Icons.Outlined.Sync
    )

    data object AdminPanel : Screen(
        "admin_panel",
        R.string.admin_panel_menu_label,
        Icons.Outlined.AdminPanelSettings
    )

    data object Flat : Screen(
        "flat",
        R.string.flat_label,
        Icons.Outlined.Cottage
    )

    data object CreateBill : Screen(
        "create_bill",
        R.string.create_bill_label,
        Icons.Outlined.Cottage
    )

    data object FlatBillList : Screen(
        "flat_bill_list",
        R.string.flat_bill_label,
        Icons.Outlined.Cottage
    )

    data object Profile : Screen(
        "profile",
        R.string.profile_label,
        Icons.Outlined.Person
    )

    companion object {
        val initialScreen = Home

        val bottomBarScreens = listOf(
            Home,
            Debts,
            Bill,
            Trips,
            Flat
        )
        private val screens = listOf(
            Home,
            Debts,
            Bill,
            Trips,
            Flat,
            FlatBillList,
            Settings,
            Notifications,
            Login,
            Test,
            AdminPanel,
            Profile,
            CreateBill,
        )

        private val bottomBarVisibleDestinations = bottomBarScreens

        private val topBarVisibleDestinations = bottomBarScreens + listOf(
            Settings, AdminPanel, Notifications, Profile, FlatBillList, CreateBill
        )

        fun isTopBarVisible(currentDestination: NavDestination?): Boolean {
            val route = currentDestination?.route

            if (route == null) return false

            return topBarVisibleDestinations.any {
                it.route == route || it.route == route.split("/").firstOrNull()
            } || route.split("?").firstOrNull() == BillFormRoute::class.qualifiedName
        }

        fun isBottomBarVisible(currentDestination: NavDestination?): Boolean {
            val route = currentDestination?.route

            if (route == null) return false

            return bottomBarVisibleDestinations.any {
                it.route == route || it.route == route.split("/").firstOrNull()
            }
        }

        fun fromRoute(route: String?): Screen? {
            if (route == null) return null

            @Suppress("SENSELESS_COMPARISON")
            return screens.firstOrNull {
                it != null && (it.route == route || it.route == route.split("/").firstOrNull())
            }
        }
    }
}