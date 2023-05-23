package com.bz.movies.presentation.mappers

import com.bz.dto.MovieDto
import com.bz.movies.presentation.screens.MovieItem

fun MovieDto.toMovieItem() = MovieItem(
    id = id,
    posterUrl = "https://image.tmdb.org/t/p/w154/$posterUrl",
    title = title,
    releaseDate = publicationDate,
    rating = rating,
)

