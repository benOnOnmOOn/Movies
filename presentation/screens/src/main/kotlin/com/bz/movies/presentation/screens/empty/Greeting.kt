package com.bz.movies.presentation.screens.empty

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.bz.movies.presentation.screens.playingNow.PlayingNowViewModel
import com.bz.movies.presentation.theme.MoviesTheme

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val playingNowViewModel : PlayingNowViewModel =  hiltViewModel()
//    val popularMoviesViewModel = koinViewModel<PopularMoviesViewModel>()

    val playingNow = playingNowViewModel.state.collectAsState()
//    val popularMovies = popularMoviesViewModel.state.collectAsState()

    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )

        Text(
            text = "Playing now !" + playingNow.value.playingNowMovies.joinToString(),
            modifier = modifier
        )

        Text(
            text = "Popular now !" + playingNow.value.playingNowMovies.joinToString(),
            modifier = modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MoviesTheme {
        Greeting("Android")
    }
}
