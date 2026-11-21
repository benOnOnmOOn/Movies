package com.bz.movies.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.serialization.Serializable

internal sealed interface Route

@Serializable
internal data object PlayingNowRoute : Route

/** Nested graph holding the Popular tab and its details screen, so the tab keeps its own stack. */
@Serializable
internal data object PopularGraph : Route

@Serializable
internal data object PopularRoute : Route

@Serializable
internal data object FavoriteRoute : Route

@Serializable
internal data object MoreRoute : Route

@Serializable
internal data class MovieDetailsRoute(val id: Int) : Route

internal fun NavController.navigateToTopLevel(route: Route) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
