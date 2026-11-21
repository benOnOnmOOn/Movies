package com.bz.movies.presentation.screens.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.bz.movies.presentation.screens.R
import com.bz.movies.presentation.screens.common.MovieDetailState
import com.bz.movies.presentation.screens.common.MoviesListContent
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.movies.presentation.screens.details.MovieDetailsScreen
import com.bz.movies.presentation.screens.favorite.FavoriteScreen
import com.bz.movies.presentation.screens.more.MoreScreen
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
        MoviesListContent(title = R.string.playing_now_screen_title, state = MoviesState())
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun PopularMoviesScreenPreview() {
    MoviesTheme {
        MoviesListContent(title = R.string.popular_now_screen_title)
    }
}
