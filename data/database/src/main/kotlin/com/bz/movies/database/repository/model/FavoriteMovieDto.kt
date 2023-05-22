package com.bz.movies.database.repository.model

data class FavoriteMovieDto(
    val id: Int,
    val posterUrl: String,
    val title: String,
    val publicationDate: String,
    val language: String,
    val rating: Int,
)
