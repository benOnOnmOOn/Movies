package com.bz.movies.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

internal const val MOVIE_ENTITY_NAME = "MOVIE_ENTITY"

@Entity(tableName = MOVIE_ENTITY_NAME)
internal data class MovieEntity(
    @PrimaryKey @ColumnInfo(name = "ID") val id: Long,
    val posterUrl: String,
    val title: String,
    val publicationDate: String,
    val language: String,
    val rating: Int
)
