package com.bz.movies.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

internal const val POPULAR_MOVIE_ENTITY_NAME = "POPULAR_MOVIE_ENTITY"

@Entity(tableName = POPULAR_MOVIE_ENTITY_NAME)
internal data class PopularMovieEntity(
    @PrimaryKey @ColumnInfo(name = "ID") val id: Long,
    val posterUrl: String,
    val title: String,
    val publicationDate: String,
    val language: String,
    val rating: Int
)
