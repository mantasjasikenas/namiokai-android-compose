package com.github.mantasjasikenas.namiokai.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Cottage
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Payments
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
    object Debts : Screen("debt", R.string.debts_name, Icons.Outlined.Payments)
    object Fuel : Screen("fuel", R.string.trips_name, Icons.Outlined.LocalGasStation)
    object Bill : Screen("bill", R.string.bill_name, Icons.Outlined.ShoppingBag)
    object Settings : Screen("settings", R.string.settings_name, Icons.Outlined.Settings)
    object Login : Screen("login", R.string.login_name, Icons.Outlined.Sync)
    object Test : Screen("test", R.string.test_name, Icons.Outlined.Sync)
    object AdminPanel :
        Screen("admin_panel", R.string.admin_panel_menu_label, Icons.Outlined.AdminPanelSettings)

    object Flat : Screen("flat", R.string.flat_label, Icons.Outlined.Cottage)

    companion object {
        val navBarScreens = listOf(
            Debts,
            Bill,
            Fuel,
            Flat
        )
        private val screens = listOf(
            Debts,
            Bill,
            Fuel,
            Flat,
            Settings,
            Login,
            Test,
            AdminPanel
        )

        fun fromRoute(route: String?): Screen =
            screens.firstOrNull { it.route == route } ?: Debts
    }
}