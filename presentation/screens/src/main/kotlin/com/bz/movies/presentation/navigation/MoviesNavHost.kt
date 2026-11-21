package com.bz.movies.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.bz.movies.presentation.screens.details.MovieDetailsScreen
import com.bz.movies.presentation.screens.favorite.FavoriteScreen
import com.bz.movies.presentation.screens.more.MoreScreen
import com.bz.movies.presentation.screens.playingNow.PlayingNowScreen
import com.bz.movies.presentation.screens.popular.PopularMoviesScreen

@Composable
internal fun MoviesNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = PlayingNowRoute
    ) {
        composable<PlayingNowRoute> {
            PlayingNowScreen()
        }
        navigation<PopularGraph>(startDestination = PopularRoute) {
            composable<PopularRoute> {
                PopularMoviesScreen(
                    onMovieClick = { id -> navController.navigate(MovieDetailsRoute(id)) }
                )
            }
            composable<MovieDetailsRoute> {
                MovieDetailsScreen(movieDetailsViewModel = hiltViewModel())
            }
        }
        composable<FavoriteRoute> {
            FavoriteScreen()
        }
        composable<MoreRoute> {
            MoreScreen(hiltViewModel())
        }
    }
}
