package com.bz.movies.presentation.screens.popular

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bz.movies.presentation.screens.common.ErrorDialog
import com.bz.movies.presentation.screens.common.MovieEffect
import com.bz.movies.presentation.screens.common.MovieEvent
import com.bz.movies.presentation.screens.common.MoviesContentWithPullToRefresh
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.movies.presentation.screens.common.NoInternetDialog
import com.bz.movies.presentation.theme.MoviesTheme
import com.bz.movies.presentation.utils.collectInLaunchedEffectWithLifecycle
import com.bz.presentation.screens.R

@Composable
internal fun PopularMoviesScreen(
    navController: NavHostController,
    popularViewModel: PopularMoviesViewModel = hiltViewModel()
) {
    val popular by popularViewModel.state.collectAsStateWithLifecycle()

    val noInternetDialog = remember { mutableStateOf(false) }
    val errorDialog = remember { mutableStateOf(false) }

    popularViewModel.effect.collectInLaunchedEffectWithLifecycle {
        when (it) {
            MovieEffect.NetworkConnectionError -> noInternetDialog.value = true
            MovieEffect.UnknownError -> errorDialog.value = true
        }
    }

    PopularMoviesScreen(
        state = popular,
        sendEvent = popularViewModel::sendEvent,
        showNoInternetDialog = noInternetDialog.value,
        showErrorDialog = errorDialog.value,
        onNetworkErrorDismiss = { noInternetDialog.value = false },
        onErrorDismiss = { errorDialog.value = false }
    ) {
        navController.navigate("tab_popular/details/$it")
    }
}

@Composable
private fun PopularMoviesScreen(
    state: MoviesState = MoviesState(),
    sendEvent: (MovieEvent) -> Unit = {},
    showNoInternetDialog: Boolean = false,
    showErrorDialog: Boolean = false,
    onNetworkErrorDismiss: () -> Unit = {},
    onErrorDismiss: () -> Unit = {},
    onMovieClicked: (id: Int) -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.popular_now_screen_title))

        MoviesContentWithPullToRefresh(
            playingNowState = state,
            refresh = { sendEvent(MovieEvent.Refresh) },
            onMovieClicked = { onMovieClicked(it.id) }
        )

        if (showNoInternetDialog) {
            NoInternetDialog(
                onDismissRequest = { onNetworkErrorDismiss() },
                onConfirmation = { onNetworkErrorDismiss() }
            )
        }

        if (showErrorDialog) {
            ErrorDialog(
                onDismissRequest = { onErrorDismiss() },
                onConfirmation = { onErrorDismiss() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PopularMoviesScreenPreview() {
    MoviesTheme {
        PopularMoviesScreen()
    }
}
