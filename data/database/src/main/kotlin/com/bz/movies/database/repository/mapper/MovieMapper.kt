package com.bz.movies.database.repository.mapper

import com.bz.movies.database.entity.MovieEntity
import com.bz.movies.database.repository.model.FavoriteMovieDto

internal fun MovieEntity.toMovieDto() = FavoriteMovieDto(
    id = id.toInt(),
    posterUrl = posterUrl,
    title = title,
    publicationDate = publicationDate,
    language = language,
    rating = rating,
)

internal fun FavoriteMovieDto.toEntity() = MovieEntity(
    id = id.toLong(),
    posterUrl = posterUrl,
    title = title,
    publicationDate = publicationDate,
    language = language,
    rating = rating,
)

