package com.github.mantasjasikenas.namiokai.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.mantasjasikenas.namiokai.presentation.sign_in.SignInViewModel
import com.github.mantasjasikenas.namiokai.ui.main.MainViewModel
import com.github.mantasjasikenas.namiokai.ui.main.sharedViewModel
import com.github.mantasjasikenas.namiokai.ui.screens.login.LoginScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    navigation(
        startDestination = Screen.Login.route,
        route = NavGraph.Auth.route
    ) {
        composable(route = Screen.Login.route) {
            val viewModel: SignInViewModel = it.sharedViewModel(navController = navController)

            LoginScreen(navController = navController,
                viewModel = viewModel,
                onSuccessfulLogin = {
                    viewModel.resetState()
                    mainViewModel.fetchDataAfterLogin()
                    navController.navigate(NavGraph.Home.route) {
                        popUpTo(NavGraph.Root.route) {
                            inclusive = true
                        }
                    }
                })

        }
    }
}