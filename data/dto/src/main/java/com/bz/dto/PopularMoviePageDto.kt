package com.bz.dto

public data class PopularMoviePageDto(
    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
    val popularMovies: List<MovieDto>
)
