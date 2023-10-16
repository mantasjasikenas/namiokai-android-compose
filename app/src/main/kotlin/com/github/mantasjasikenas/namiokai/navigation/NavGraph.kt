package com.github.mantasjasikenas.namiokai.navigation

import androidx.annotation.StringRes
import com.github.mantasjasikenas.namiokai.R

sealed class NavGraph(
    val route: String,
    @StringRes val titleResourceId: Int,
) {
    data object Root : NavGraph("root_graph", R.string.root_graph_label)
    data object Home : NavGraph("home_graph", R.string.home_graph_label)
    data object Auth : NavGraph("auth_graph", R.string.auth_graph_label)

    companion object {

        private val initialGraph = Root
        private val Graphs = listOf(
            Root,
            Home,
            Auth
        )

        fun fromRoute(route: String?): NavGraph =
            Graphs.firstOrNull { it.route == route } ?: initialGraph
    }
}