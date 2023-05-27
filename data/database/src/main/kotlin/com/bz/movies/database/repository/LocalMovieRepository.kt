package com.bz.movies.database.repository

import com.bz.dto.MovieDto
import kotlinx.coroutines.flow.Flow


interface LocalMovieRepository {

    val favoritesMovies: Flow<Result<List<MovieDto>>>
    suspend fun getFavoritesMovies(): Result<List<MovieDto>>

    suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit>

    suspend fun deleteFavoriteMovie(movieDto: MovieDto): Result<Unit>

}
