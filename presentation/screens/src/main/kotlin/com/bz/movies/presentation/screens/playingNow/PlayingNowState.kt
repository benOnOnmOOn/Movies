package com.bz.movies.presentation.screens.playingNow

import com.bz.movies.presentation.screens.MovieItem

data class PlayingNowState(
    val isLoading: Boolean = true,
    val playingNowMovies: List<MovieItem> = emptyList()
)

