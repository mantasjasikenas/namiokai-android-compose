package com.example.namiokai.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.namiokai.R

sealed class Screen(
    val route: String,
    @StringRes val titleResourceId: Int,
    val imageVector: ImageVector = Icons.Outlined.BrokenImage
) {
    object Summary : Screen("start", R.string.app_name, Icons.Outlined.Payments)
    object Fuel : Screen("fuel", R.string.fuel_name, Icons.Outlined.LocalGasStation)
    object Bill : Screen("bill", R.string.bill_name, Icons.Outlined.ShoppingBag)
    object Settings : Screen("settings", R.string.settings_name, Icons.Outlined.Settings)
    object Auth : Screen("auth", R.string.auth_name, Icons.Outlined.Sync)
    object Test : Screen("test", R.string.test_name, Icons.Outlined.Sync)
    object AdminPanel : Screen("admin_panel", R.string.admin_panel_menu_label, Icons.Outlined.AdminPanelSettings)

    companion object {
        val navBarScreens = listOf(
            Summary,
            Bill,
            Fuel
        )
        private val screens = listOf(
            Summary,
            Bill,
            Fuel,
            Settings,
            Auth,
            Test,
            AdminPanel
        )

        fun fromRoute(route: String?): Screen =
            screens.firstOrNull { it.route == route } ?: Summary
    }
}