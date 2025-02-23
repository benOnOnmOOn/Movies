package com.bz.movies.presentation.mappers

import com.bz.dto.MovieDto
import com.bz.movies.presentation.screens.common.MovieItem

internal const val BASE_URL = "https://image.tmdb.org/t/p/w154/"

internal fun MovieDto.toMovieItem() = MovieItem(
    id = id,
    posterUrl = "$BASE_URL$posterUrl",
    title = title,
    releaseDate = publicationDate,
    rating = rating,
    language = language
)

internal fun MovieItem.toDTO() = MovieDto(
    rating = rating,
    language = language,
    title = title,
    publicationDate = releaseDate,
    id = id,
    posterUrl = posterUrl.removePrefix(BASE_URL)
)
