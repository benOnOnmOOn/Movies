package com.bz.movies.presentation.screens.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.bz.movies.presentation.screens.common.MovieDetailState
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.movies.presentation.screens.details.MovieDetailsScreen
import com.bz.movies.presentation.screens.favorite.FavoriteScreen
import com.bz.movies.presentation.screens.more.MoreScreen
import com.bz.movies.presentation.screens.playingNow.PlayingNowScreen
import com.bz.movies.presentation.screens.popular.PopularMoviesScreen
import com.bz.movies.presentation.theme.MoviesTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun MovieDetailsScreenPreview() {
    MoviesTheme {
        MovieDetailsScreen(state = MovieDetailState())
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun FavoriteScreenPreview() {
    MoviesTheme {
        FavoriteScreen(state = MoviesState()) {}
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun MoreScreenPreview() {
    MoviesTheme {
        MoreScreen {}
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PlayingNowScreenPreview() {
    MoviesTheme {
        PlayingNowScreen(
            state = MoviesState(),
            showNoInternetDialog = false,
            showErrorDialog = false,
            onNetworkErrorDismiss = {},
            onErrorDismiss = {},
            sendEvent = {}
        )
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PopularMoviesScreenPreview() {
    MoviesTheme {
        PopularMoviesScreen()
    }
}
