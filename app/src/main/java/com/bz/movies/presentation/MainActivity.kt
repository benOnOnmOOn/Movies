@file:Suppress("FunctionNaming")
package com.bz.movies.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bz.movies.presentation.screens.playingNow.PlayingNowViewModel
import com.bz.movies.presentation.screens.popular.PopularMoviesViewModel
import com.bz.movies.presentation.theme.MoviesTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoviesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val playingNowViewModel = koinViewModel<PlayingNowViewModel>()
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
