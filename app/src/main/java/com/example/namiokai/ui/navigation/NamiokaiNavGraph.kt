package com.example.namiokai.ui

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.namiokai.ui.main.MainViewModel
import com.example.namiokai.ui.navigation.Screen
import com.example.namiokai.ui.screens.BillScreen
import com.example.namiokai.ui.screens.FuelScreen
import com.example.namiokai.ui.screens.DebtsScreen
import com.example.namiokai.ui.screens.admin.AdminPanelScreen
import com.example.namiokai.ui.screens.auth.AuthScreen
import com.example.namiokai.ui.screens.settings.SettingsScreen
import com.example.namiokai.ui.screens.test.TestScreen


fun NavGraphBuilder.namiokaiNavigationGraph(navController: NavHostController, mainViewModel: MainViewModel) {
    composable(route = Screen.Debts.route) {
        //val viewModel = hiltViewModel<MainViewModel>()
        DebtsScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Bill.route) { backStackEntry ->
        /*val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.Summary.route)
        }
        val parentViewModel = hiltViewModel<MainViewModel>(parentEntry)*/
        BillScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Fuel.route) { backStackEntry ->
        /*val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(Screen.Summary.route)
        }
        val parentViewModel = hiltViewModel<MainViewModel>(parentEntry)*/
        FuelScreen(mainViewModel = mainViewModel)
    }
    composable(route = Screen.Settings.route) {
        SettingsScreen()
    }
    composable(route = Screen.Auth.route) {
        AuthScreen(navController = navController)
    }
    composable(route = Screen.Test.route) {
        TestScreen()
    }
    composable(route = Screen.AdminPanel.route) {
        AdminPanelScreen()
    }
}
