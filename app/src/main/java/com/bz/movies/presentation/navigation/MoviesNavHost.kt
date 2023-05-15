package com.bz.movies.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bz.movies.presentation.screens.empty.Greeting
import com.bz.movies.presentation.screens.playingNow.PlayingNowScreen


@Composable
fun MoviesNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RootRoute.PlayingNow.route,
    ) {
        composable(RootRoute.PlayingNow.route) {
            PlayingNowScreen()
        }
        composable(RootRoute.Popular.route) {
            Greeting(RootRoute.Popular.route)
        }
        composable(RootRoute.Favorite.route) {
            Greeting(RootRoute.Favorite.route)
        }
        composable(RootRoute.More.route) {
            Greeting(RootRoute.More.route)
        }
    }
}



