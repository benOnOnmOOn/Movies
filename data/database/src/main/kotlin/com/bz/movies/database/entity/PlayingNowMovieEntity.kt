package com.bz.movies.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
internal const val PLAYING_NOW_MOVIE_NAME = "PLAYING_NOW_MOVIE_ENTITY"

@Entity(tableName = PLAYING_NOW_MOVIE_NAME)
internal data class PlayingNowMovieEntity(
    @PrimaryKey @ColumnInfo(name = "ID") val id: Long,
    val posterUrl: String,
    val title: String,
    val publicationDate: String,
    val language: String,
    val rating: Int
)
