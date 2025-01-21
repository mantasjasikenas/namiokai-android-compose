package com.github.mantasjasikenas.namiokai.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.github.mantasjasikenas.core.common.util.SnackbarController
import com.github.mantasjasikenas.core.domain.model.SharedState
import com.github.mantasjasikenas.core.domain.model.isNotLoggedIn
import com.github.mantasjasikenas.namiokai.navigation.TopLevelRoute
import com.github.mantasjasikenas.namiokai.navigation.navigateToTopLevelRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

@Composable
fun rememberNamiokaiAppState(
    sharedState: SharedState,
    onNavigateToAuthGraph: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): NamiokaiAppState {
    UserLoginLaunchedEffect(
        sharedState = sharedState,
        onNavigateToAuthGraph = onNavigateToAuthGraph
    )

    ObserveSnackbarEvents(
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope
    )

    return remember(
        navController,
        coroutineScope,
    ) {
        NamiokaiAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            snackbarHostState = snackbarHostState,
        )
    }
}

@Stable
class NamiokaiAppState(
    val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
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
            val isTopLevelRoute = isTopLevelRoute

            return remember(isTopLevelRoute) {
                isTopLevelRoute
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
        navController.navigateToTopLevelRoute(topLevelRoute)
    }

    val topLevelDestinations: List<TopLevelRoute> = TopLevelRoute.routes
}

@Composable
private fun UserLoginLaunchedEffect(sharedState: SharedState, onNavigateToAuthGraph: () -> Unit) {
    LaunchedEffect(key1 = sharedState.currentUser) {
        if (sharedState.currentUser.isNotLoggedIn()) {
            onNavigateToAuthGraph()
        }
    }
}

@Composable
private fun ObserveSnackbarEvents(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    ObserveAsEvents(
        flow = SnackbarController.events,
        snackbarHostState
    ) { event ->
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.action?.name,
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed) {
                event.action?.action?.invoke()
            }
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

@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner.lifecycle, key1, key2, flow) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}