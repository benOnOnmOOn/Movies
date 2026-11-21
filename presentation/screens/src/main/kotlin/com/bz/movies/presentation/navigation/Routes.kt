package com.bz.movies.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.serialization.Serializable

internal sealed interface TopLevelRoute

@Serializable
internal data object PlayingNowRoute : TopLevelRoute

/** Nested graph holding the Popular tab and its details screen, so the tab keeps its own stack. */
@Serializable
internal data object PopularGraph : TopLevelRoute

@Serializable
internal data object PopularRoute : TopLevelRoute

@Serializable
internal data object FavoriteRoute : TopLevelRoute

@Serializable
internal data object MoreRoute : TopLevelRoute

@Serializable
internal data class MovieDetailsRoute(val id: Int) : TopLevelRoute

internal fun NavController.navigateToTopLevel(route: TopLevelRoute) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
