package com.bz.movies.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity

internal const val CACHED_MOVIE_ENTITY_NAME = "CACHED_MOVIE_ENTITY"

internal object MovieCategory {
    const val PLAYING_NOW = "PLAYING_NOW"
    const val POPULAR = "POPULAR"
}

@Entity(tableName = CACHED_MOVIE_ENTITY_NAME, primaryKeys = ["category", "ID"])
internal data class CachedMovieEntity(
    val category: String,
    @ColumnInfo(name = "ID") val id: Long,
    val posterUrl: String,
    val title: String,
    val publicationDate: String,
    val language: String,
    val rating: Int
)
