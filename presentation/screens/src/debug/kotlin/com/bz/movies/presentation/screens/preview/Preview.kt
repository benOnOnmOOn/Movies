package com.bz.movies.presentation.screens.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bz.movies.presentation.screens.common.MovieDetailState
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.movies.presentation.screens.details.MovieDetailsScreen
import com.bz.movies.presentation.screens.favorite.FavoriteScreen
import com.bz.movies.presentation.screens.more.MoreScreen
import com.bz.movies.presentation.screens.playingNow.PlayingNowScreen
import com.bz.movies.presentation.screens.popular.PopularMoviesScreen
import com.bz.movies.presentation.theme.MoviesTheme

@Preview(showBackground = true)
@Composable
private fun MovieDetailsScreenPreview() {
    MoviesTheme {
        MovieDetailsScreen(state = MovieDetailState())
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteScreenPreview() {
    MoviesTheme {
        FavoriteScreen(state = MoviesState()) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun MoreScreenPreview() {
    MoviesTheme {
        MoreScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayingNowScreenPreview() {
    MoviesTheme {
        PlayingNowScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun PopularMoviesScreenPreview() {
    MoviesTheme {
        PopularMoviesScreen()
    }
}
