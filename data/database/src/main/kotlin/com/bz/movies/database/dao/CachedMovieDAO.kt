package com.bz.movies.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.bz.movies.database.entity.CACHED_MOVIE_ENTITY_NAME
import com.bz.movies.database.entity.CachedMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CachedMovieDAO : BaseDao<CachedMovieEntity> {
    @Query("SELECT * FROM $CACHED_MOVIE_ENTITY_NAME WHERE category = :category")
    fun observeMovies(category: String): Flow<List<CachedMovieEntity>>

    @Query("DELETE FROM $CACHED_MOVIE_ENTITY_NAME WHERE category = :category")
    suspend fun clear(category: String)
}
