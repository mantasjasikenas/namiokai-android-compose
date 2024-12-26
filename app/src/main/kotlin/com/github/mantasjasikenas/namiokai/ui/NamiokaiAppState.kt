package com.github.mantasjasikenas.namiokai.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.isNotLoggedIn
import com.github.mantasjasikenas.namiokai.navigation.TopLevelRoute
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

@Composable
fun rememberNamiokaiAppState(
    sharedState: SharedState,
    onNavigateToAuthGraph: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): NamiokaiAppState {
    UserLoginLaunchedEffect(
        sharedState = sharedState,
        onNavigateToAuthGraph = onNavigateToAuthGraph
    )

    return remember(
        navController,
        coroutineScope,
    ) {
        NamiokaiAppState(
            navController = navController,
            coroutineScope = coroutineScope,
        )
    }
}

@Stable
class NamiokaiAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }


    val currentTopLevelRoute: TopLevelRoute?
        @Composable get() {
            return currentDestination?.getTopLevelRoute()
        }

    val isTopLevelRoute: Boolean
        @Composable get() {
            return currentTopLevelRoute != null
        }

    val showBottomBar: Boolean
        @Composable get() {
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val isTopLevelRoute = isTopLevelRoute

            return remember(isLandscape, isTopLevelRoute) {
                !isLandscape && isTopLevelRoute
            }
        }

    val showTopBar: Boolean
        @Composable get() {
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            return remember(isLandscape) {
                !isLandscape
            }
        }

    fun navigateToTopLevelRoute(topLevelRoute: TopLevelRoute) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        navController.navigate(topLevelRoute.route, topLevelNavOptions)
    }
}

@Composable
private fun UserLoginLaunchedEffect(sharedState: SharedState, onNavigateToAuthGraph: () -> Unit) {
    LaunchedEffect(key1 = sharedState.currentUser) {
        if (sharedState.currentUser.isNotLoggedIn()) {
            onNavigateToAuthGraph()
        }
    }
}

@SuppressLint("RestrictedApi")
internal fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route.qualifiedName.toString(), null)
    } == true

internal fun NavDestination?.getTopLevelRoute(): TopLevelRoute? {
    return TopLevelRoute.routes.firstOrNull {
        this?.isRouteInHierarchy(it.route::class) == true
    }
}