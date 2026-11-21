package com.bz.movies.presentation.screens.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bz.movies.presentation.utils.collectInLaunchedEffectWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun MoviesListScreen(
    @StringRes title: Int,
    stateFlow: StateFlow<MoviesState>,
    effects: Flow<MovieEffect>,
    onRefresh: () -> Unit,
    onMovieClicked: (MovieItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by stateFlow.collectAsStateWithLifecycle()
    var error by remember { mutableStateOf<MovieEffect?>(null) }

    effects.collectInLaunchedEffectWithLifecycle { error = it }

    MoviesListContent(
        title = title,
        state = state,
        error = error,
        onRefresh = onRefresh,
        onMovieClicked = onMovieClicked,
        onErrorDismiss = { error = null },
        modifier = modifier
    )
}

@Composable
internal fun MoviesListContent(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    state: MoviesState = MoviesState(),
    error: MovieEffect? = null,
    onRefresh: () -> Unit = {},
    onMovieClicked: (MovieItem) -> Unit = {},
    onErrorDismiss: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(title))

        MoviesContentWithPullToRefresh(
            playingNowState = state,
            refresh = onRefresh,
            onMovieClicked = onMovieClicked
        )

        error?.let { ErrorDialog(effect = it, onDismiss = onErrorDismiss) }
    }
}
