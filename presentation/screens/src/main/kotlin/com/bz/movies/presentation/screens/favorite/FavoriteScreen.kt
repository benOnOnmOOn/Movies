package com.bz.movies.presentation.screens.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bz.movies.presentation.screens.R
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MoviesContent
import com.bz.movies.presentation.screens.common.MoviesState

@Composable
internal fun FavoriteScreen(favoriteScreenViewModel: FavoriteScreenViewModel = hiltViewModel()) {
    val playingNow by favoriteScreenViewModel.state.collectAsStateWithLifecycle()
    FavoriteScreen(playingNow, favoriteScreenViewModel::sendEvent)
}

@Composable
internal fun FavoriteScreen(state: MoviesState = MoviesState(), sendEvent: (MovieEvent) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.your_favorite_movies))

        MoviesContent(playingNowState = state) {
            sendEvent(MovieEvent.OnMovieClicked(it))
        }
    }
}
