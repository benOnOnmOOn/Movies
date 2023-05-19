package com.bz.network.repository.model

data class MovieDto(
    val id: Int,
    val posterUrl: String,
    val title: String,
    val publicationDate: String,
    val language: String,
    val rating: Int,
)
