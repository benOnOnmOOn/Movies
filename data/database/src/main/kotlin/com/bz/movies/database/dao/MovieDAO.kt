package com.bz.movies.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.bz.movies.database.entity.MovieEntity

@Dao
internal interface MovieDAO : BaseDao<MovieEntity> {

    @Query("SELECT * FROM ${MovieEntity.ENTITY_NAME}")
    suspend fun getAllMovies(): List<MovieEntity>

}
