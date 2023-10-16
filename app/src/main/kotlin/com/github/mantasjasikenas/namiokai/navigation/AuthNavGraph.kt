package com.github.mantasjasikenas.namiokai.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.mantasjasikenas.feature.login.LoginScreen
import com.github.mantasjasikenas.feature.login.SignInViewModel
import com.github.mantasjasikenas.namiokai.MainActivityViewModel

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    @Suppress("UNUSED_PARAMETER")
    mainActivityViewModel: MainActivityViewModel
) {
    navigation(
        startDestination = Screen.Login.route,
        route = NavGraph.Auth.route
    ) {
        composable(route = Screen.Login.route) {
            val viewModel: SignInViewModel = hiltViewModel()

            LoginScreen(viewModel = viewModel,
                onSuccessfulLogin = {
                    viewModel.resetState()
                    navController.navigate(NavGraph.Home.route) {
                        popUpTo(NavGraph.Root.route) {
                            inclusive = false
                        }
                    }
                })

        }
    }
}