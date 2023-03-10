package com.github.mantasjasikenas.namiokai.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.screens.admin.AdminPanelScreen
import com.github.mantasjasikenas.namiokai.ui.screens.auth.AuthScreen
import com.github.mantasjasikenas.namiokai.ui.screens.bill.BillScreen
import com.github.mantasjasikenas.namiokai.ui.screens.debts.DebtsScreen
import com.github.mantasjasikenas.namiokai.ui.screens.flat.FlatScreen
import com.github.mantasjasikenas.namiokai.ui.screens.fuel.FuelScreen
import com.github.mantasjasikenas.namiokai.ui.screens.settings.SettingsScreen
import com.github.mantasjasikenas.namiokai.ui.screens.test.TestScreen


fun NavGraphBuilder.namiokaiNavigationGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    composable(route = Screen.Debts.route) {
        DebtsScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Bill.route) {
        BillScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Fuel.route) {
        FuelScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Flat.route) {
        FlatScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Settings.route) {
        SettingsScreen(navController = navController, mainViewModel = mainViewModel)
    }
    composable(route = Screen.Login.route) {
        AuthScreen(navController = navController, mainViewModel = mainViewModel)
    }
    composable(route = Screen.Test.route) {
        TestScreen()
    }
    composable(route = Screen.AdminPanel.route) {
        AdminPanelScreen()
    }
}
