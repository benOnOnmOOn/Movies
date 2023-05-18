package com.bz.movies.presentation.screens.playingNow

data class PlayingNowState(
    val isLoading: Boolean = true,
    val playingNowMovies: List<PlayingNowMovieItem> = emptyList()
)

data class PlayingNowMovieItem(
    val id: Int,
    val posterUrl: String,
)
