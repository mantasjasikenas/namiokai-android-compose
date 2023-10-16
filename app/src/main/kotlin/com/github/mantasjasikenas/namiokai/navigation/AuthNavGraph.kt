package com.github.mantasjasikenas.namiokai.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.mantasjasikenas.feature.login.LoginScreen
import com.github.mantasjasikenas.feature.login.LoginViewModel
import com.github.mantasjasikenas.namiokai.MainActivityViewModel

fun NavGraphBuilder.authNavGraph(
    @Suppress("UNUSED_PARAMETER")
    mainActivityViewModel: MainActivityViewModel,
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