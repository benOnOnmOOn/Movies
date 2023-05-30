package com.bz.movies.database.repository

import com.bz.dto.MovieDto
import kotlinx.coroutines.flow.Flow


interface LocalMovieRepository {

    val favoritesMovies: Flow<Result<List<MovieDto>>>

    val playingNowMovies: Flow<Result<List<MovieDto>>>

    val popularMovies: Flow<Result<List<MovieDto>>>

    suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit>

    suspend fun deleteFavoriteMovie(movieDto: MovieDto): Result<Unit>

    suspend fun insertPlayingNowMovies(movieDto: List<MovieDto>): Result<Unit>

    suspend fun clearPlayingNowMovies(): Result<Unit>

    suspend fun insertPopularMovies(movieDto: List<MovieDto>): Result<Unit>

    suspend fun clearPopularMovies(): Result<Unit>

}
