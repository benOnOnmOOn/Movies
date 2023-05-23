package com.bz.movies.database.repository.mapper

import com.bz.dto.MovieDto
import com.bz.movies.database.entity.MovieEntity

internal fun MovieEntity.toMovieDto() = MovieDto(
    id = id.toInt(),
    posterUrl = posterUrl,
    title = title,
    publicationDate = publicationDate,
    language = language,
    rating = rating,
)

internal fun MovieDto.toEntity() = MovieEntity(
    id = id.toLong(),
    posterUrl = posterUrl,
    title = title,
    publicationDate = publicationDate,
    language = language,
    rating = rating,
)

