package com.bz.movies.presentation.screens.common

import androidx.compose.runtime.Immutable

@Immutable internal data class MoviesState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val playingNowMovies: List<MovieItem> = emptyList()
)

@Immutable internal data class MovieDetailState(
    val isLoading: Boolean = true,
    val movieDetails: MovieItem? = null
)
