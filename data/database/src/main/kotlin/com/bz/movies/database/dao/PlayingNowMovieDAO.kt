package com.bz.movies.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.bz.movies.database.entity.PLAYING_NOW_MOVIE_NAME
import com.bz.movies.database.entity.PlayingNowMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PlayingNowMovieDAO : BaseDao<PlayingNowMovieEntity> {
    @Query("SELECT * FROM $PLAYING_NOW_MOVIE_NAME")
    fun observeAllMovies(): Flow<List<PlayingNowMovieEntity>>

    @Query("DELETE FROM $PLAYING_NOW_MOVIE_NAME")
    fun clearTable()
}
