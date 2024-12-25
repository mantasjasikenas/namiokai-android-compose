package com.github.mantasjasikenas.namiokai.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.github.mantasjasikenas.feature.login.navigation.LoginRoute
import com.github.mantasjasikenas.feature.login.navigation.loginScreen

fun NavGraphBuilder.authNavGraph(
    onSuccessfulLogin: () -> Unit
) {
    navigation<Route.AuthGraph>(
        startDestination = LoginRoute
    ) {
        loginScreen(
            onSuccessfulLogin = onSuccessfulLogin
        )
    }
}