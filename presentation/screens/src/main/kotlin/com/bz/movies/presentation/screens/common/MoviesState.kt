package com.bz.movies.presentation.screens.common

data class MoviesState(
    val isLoading: Boolean = true,
    val playingNowMovies: List<MovieItem> = emptyList()
)

