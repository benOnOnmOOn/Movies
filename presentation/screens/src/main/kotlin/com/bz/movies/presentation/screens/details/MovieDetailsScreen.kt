package com.bz.movies.presentation.screens.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bz.movies.presentation.screens.R
import com.bz.movies.presentation.screens.common.MovieDetailState

@Composable
internal fun MovieDetailsScreen(
    id: Int?,
    movieDetailsViewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val playingNow by movieDetailsViewModel.state.collectAsStateWithLifecycle()

    @Suppress("DeprecatedCall")
    LaunchedEffect(id) {
        if (id != null) {
            movieDetailsViewModel.fetchMovieDetails(id)
        }
    }
    MovieDetailsScreen(playingNow)
}

@Composable
internal fun MovieDetailsScreen(state: MovieDetailState = MovieDetailState()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = stringResource(R.string.details_screen_title))

        state.movieDetails?.let {
            Text(text = it.title)
            Text(text = it.releaseDate)
            Text(text = it.language)
        }
    }
}
