package com.bz.movies.presentation.screens.popular

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.bz.movies.presentation.theme.MoviesTheme

@Composable
fun PopularMoviesScreen(
    playingNowViewModel: PopularMoviesViewModel = hiltViewModel()
) {
    val playingNow by playingNowViewModel.state.collectAsState()
    PopularMoviesScreen(playingNow)
}

@Composable
private fun PopularMoviesScreen(
    state: PopularMoviesState = PopularMoviesState(),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "PlayingNow !!")

        PopularMoviesContent(playingNowState = state) {

        }

    }

}

@Preview(showBackground = true)
@Composable
private fun PopularMoviesScreen() {
    MoviesTheme {
        PopularMoviesScreen()
    }
}
