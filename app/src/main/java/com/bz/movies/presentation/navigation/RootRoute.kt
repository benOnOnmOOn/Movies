package com.bz.movies.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination


enum class RootRoute(val route: String) {
    PlayingNow("tab_playing_now"),
    Popular("tab_popular"),
    Favorite("tab_favorite"),
    More("tab_more"),
}

fun NavController.navigateToRootRoute(rootRoute: RootRoute){
    navigate(rootRoute.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}
