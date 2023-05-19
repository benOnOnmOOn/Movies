package com.bz.movies.presentation.screens

data class MovieItem(
    val id: Int,
    val posterUrl: String,
    val title: String,
    val releaseDate: String,
    val rating: Int,
)
