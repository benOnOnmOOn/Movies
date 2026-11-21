package com.bz.movies.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.serialization.Serializable

@Serializable
internal data object PlayingNowRoute

/** Nested graph holding the Popular tab and its details screen, so the tab keeps its own stack. */
@Serializable
internal data object PopularGraph

@Serializable
internal data object PopularRoute

@Serializable
internal data object FavoriteRoute

@Serializable
internal data object MoreRoute

@Serializable
internal data class MovieDetailsRoute(val id: Int)

internal fun NavController.navigateToTopLevel(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
