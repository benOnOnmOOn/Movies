package com.bz.movies.presentation.screens.popular

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bz.presentation.screens.R

@Composable
fun PopularMoviesContent(
    playingNowState: PopularMoviesState,
    refresh: () -> Unit,
) {

    var isFromPullUp by remember { mutableStateOf(false) }
    val isLoadingFromPullUp by remember {
        derivedStateOf { isFromPullUp && playingNowState.isLoading }
    }

    fun refresh(isPullUp: Boolean) {
        isFromPullUp = isPullUp
        refresh()
    }

    val pullRefreshState = rememberPullRefreshState(isLoadingFromPullUp, { refresh(true) })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(playingNowState.playingNowMovies) { movieItem ->

                PopularMoviesItemContent(movieItem)

                Spacer(modifier = Modifier.height(12.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                )
            }
        }


        PullRefreshIndicator(
            isLoadingFromPullUp,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )

    }

}

@Composable
fun PopularMoviesItemContent(movieItem: PopularMovieItem) =
    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp),
    ) {
        AsyncImage(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
                .padding(4.dp),
            model = movieItem.posterUrl,
            contentDescription = movieItem.title,
        )
        Column(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .weight(1f)
        ) {
            Text(text = movieItem.title)
            Text(text = movieItem.releaseDate)
            Text(text = "${movieItem.rating}/100")
        }

        Image(
            painterResource(R.drawable.ic_star),
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
                .padding(4.dp),
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
