package com.bz.movies.presentation.screens.popular

import com.bz.movies.presentation.screens.MovieItem

data class PopularMoviesState(
    val isLoading: Boolean = true,
    val playingNowMovies: List<MovieItem> = emptyList()
)

