package com.bz.dto

data class MoveDetailDto(
    val id: Int,
    val posterUrl: String,
    val publicationDate: String,
    val language: String,
    val title: String,
    val genre: Set<String>,
    val overview: String
)
