package com.bz.movies.presentation.screens.playingNow

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.bz.movies.presentation.screens.common.MoviesContent
import com.bz.movies.presentation.screens.common.MoviesState
import com.bz.movies.presentation.theme.MoviesTheme

@Composable
fun PlayingNowScreen(
    playingNowViewModel: PlayingNowViewModel = hiltViewModel()
) {
    val playingNow by playingNowViewModel.state.collectAsState()
    PlayingNowScreen(playingNow)
}

@Composable
private fun PlayingNowScreen(
    state: MoviesState = MoviesState(),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "PlayingNow !!")

        MoviesContent(playingNowState = state) {

        }

    }

}

@Preview(showBackground = true)
@Composable
private fun PlayingNowScreen() {
    MoviesTheme {
        PlayingNowScreen()
    }
}
