package com.bz.movies.presentation.screens.popular

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bz.movies.presentation.screens.R
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MoviesListScreen

@Composable
internal fun PopularMoviesScreen(
    onMovieClick: (id: Int) -> Unit,
    popularViewModel: PopularMoviesViewModel = hiltViewModel()
) {
    MoviesListScreen(
        title = R.string.popular_now_screen_title,
        stateFlow = popularViewModel.state,
        effects = popularViewModel.effect,
        onRefresh = { popularViewModel.sendEvent(MovieEvent.Refresh) },
        onMovieClicked = { onMovieClick(it.id) }
    )
}
