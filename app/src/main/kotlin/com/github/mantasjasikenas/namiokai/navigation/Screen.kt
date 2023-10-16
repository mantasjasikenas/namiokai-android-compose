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

    data object Profile : Screen(
        "profile",
        R.string.profile_label,
        Icons.Outlined.Person
    )

    companion object {
        val initialScreen = Home

        val navBarScreens = listOf(
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
            Settings,
            Notifications,
            Login,
            Test,
            AdminPanel,
            Profile
        )

        //private val screens = Screen::class.sealedSubclasses.map { it.objectInstance }

        fun fromRoute(route: String?): Screen {
            if (route == null) return initialScreen

            @Suppress("SENSELESS_COMPARISON")
            return screens.firstOrNull { it != null && it.route == route } ?: initialScreen
        }
    }
}