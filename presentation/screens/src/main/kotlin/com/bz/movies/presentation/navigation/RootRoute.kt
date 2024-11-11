package com.bz.movies.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

internal enum class RootRoute(val route: String) {
    PlayingNow("tab_playing_now"),
    Popular("tab_popular"),
    Favorite("tab_favorite"),
    More("tab_more"),
    Details("tab_popular/details/{id}")
}

internal fun NavController.navigateToRootRoute(rootRoute: RootRoute) {
    navigate(rootRoute.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = false
    }
}

@Stable
@Composable
internal fun currentRootRouteAsState(navController: NavController): State<RootRoute> {
    val selectedItem = remember { mutableStateOf(RootRoute.PlayingNow) }

    DisposableEffect(navController) {
        val listener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                val item = RootRoute.entries.find { it.route == destination.route }
                selectedItem.value = item ?: RootRoute.PlayingNow
            }
        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}
