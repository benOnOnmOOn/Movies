package com.bz.movies.presentation.screens.playingNow

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bz.movies.presentation.screens.R
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MoviesListScreen

@Composable
internal fun PlayingNowScreen(playingNowViewModel: PlayingNowViewModel = hiltViewModel()) {
    MoviesListScreen(
        title = R.string.playing_now_screen_title,
        stateFlow = playingNowViewModel.state,
        effects = playingNowViewModel.effect,
        onRefresh = { playingNowViewModel.sendEvent(MovieEvent.Refresh) },
        onMovieClicked = { playingNowViewModel.sendEvent(MovieEvent.OnMovieClicked(it)) }
    )
}
