package com.github.mantasjasikenas.feature.login.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.github.mantasjasikenas.feature.login.LoginRoute
import com.github.mantasjasikenas.feature.login.LoginViewModel
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

fun NavGraphBuilder.loginScreen(
    onSuccessfulLogin: () -> Unit
) {
    composable<LoginRoute> {
        val viewModel: LoginViewModel = hiltViewModel()

        LoginRoute(
            viewModel = viewModel,
            onSuccessfulLogin = {
                viewModel.resetState()
                onSuccessfulLogin()
            }
        )
    }
}