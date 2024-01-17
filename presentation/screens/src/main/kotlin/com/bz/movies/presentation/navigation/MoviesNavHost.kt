package com.bz.movies.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bz.movies.presentation.screens.details.MovieDetailsScreen
import com.bz.movies.presentation.screens.favorite.FavoriteScreen
import com.bz.movies.presentation.screens.more.MoreScreen
import com.bz.movies.presentation.screens.playingNow.PlayingNowScreen
import com.bz.movies.presentation.screens.popular.PopularMoviesScreen

@Composable
fun MoviesNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = RootRoute.PlayingNow.route
    ) {
        composable(RootRoute.PlayingNow.route) {
            PlayingNowScreen()
        }
        composable(RootRoute.Popular.route) {
            PopularMoviesScreen(hiltViewModel(), navController)
        }
        composable(RootRoute.Favorite.route) {
            FavoriteScreen()
        }
        composable(RootRoute.More.route) {
            MoreScreen(hiltViewModel())
        }
        composable(
            route = RootRoute.Details.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            MovieDetailsScreen(backStackEntry.arguments?.getInt("id"))
        }
    }
}
