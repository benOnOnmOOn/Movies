package com.bz.network.repository.model

data class PopularMoviePageDto(
    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
    val popularMovies: List<PopularMovieDto>,
)
