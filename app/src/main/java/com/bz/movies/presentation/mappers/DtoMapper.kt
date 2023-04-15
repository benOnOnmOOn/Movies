package com.bz.movies.presentation.mappers

import com.bz.movies.presentation.screens.playingNow.PlayingNowMovieItem
import com.bz.movies.presentation.screens.popular.PopularMovieItem
import com.bz.network.repository.model.PlayingNowMovieDto
import com.bz.network.repository.model.PopularMovieDto

fun PopularMovieDto.toPopularMovieItem() = PopularMovieItem(
    id = id,
    posterUrl = "https://image.tmdb.org/t/p/w154/$posterUrl",
    title = title,
    releaseDate = publicationDate,
    rating = rating,
)

fun PlayingNowMovieDto.toPlayingNowMovieItem() = PlayingNowMovieItem(
    id = id,
    posterUrl =  "https://image.tmdb.org/t/p/w154/$posterUrl",
)