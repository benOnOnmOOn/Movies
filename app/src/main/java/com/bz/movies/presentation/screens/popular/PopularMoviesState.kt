package com.bz.movies.presentation.screens.popular

data class PopularMoviesState(
    val isLoading: Boolean = true,
    val playingNowMovies: List<PopularMovieItem> = emptyList()
)

data class PopularMovieItem(
    val id: Int,
    val posterUrl: String,
    val title: String,
    val releaseDate: String,
    val rating: Int,
)