package com.github.mantasjasikenas.namiokai.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.mantasjasikenas.feature.login.LoginScreen
import com.github.mantasjasikenas.feature.login.LoginViewModel

fun NavGraphBuilder.authNavGraph(
    onSuccessfulLogin: () -> Unit
) {
    navigation(
        startDestination = Screen.Login.route,
        route = NavGraph.Auth.route
    ) {
        composable(route = Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()

            LoginScreen(viewModel = viewModel,
                onSuccessfulLogin = {
                    viewModel.resetState()
                    onSuccessfulLogin()
                })

        }
    }
}