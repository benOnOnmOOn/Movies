package com.bz.movies.database.repository

import com.bz.dto.MovieDto
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.entity.MovieEntity
import com.bz.movies.database.repository.mapper.toEntity
import com.bz.movies.database.repository.mapper.toMovieDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


internal class LocalMovieRepositoryImpl(
    private val movieDAO: MovieDAO
) : LocalMovieRepository {
    override suspend fun getFavoritesMovies(): Result<List<MovieDto>> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO
                    .getAllMovies()
                    .map(MovieEntity::toMovieDto)
            }
        }

    override suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO.insert(movieDto.toEntity())
            }
        }


    override val favoritesMovies2: Flow<Result<List<MovieDto>>>
        get() = runCatching { movieDAO.observeAllMovies().map { it.map(MovieEntity::toMovieDto) } }
            .fold(
                onSuccess = { it.map(Result.Companion::success) },
                onFailure = { flowOf(Result.failure(it)) }
            )

    override val favoritesMovies: Flow<List<MovieDto>> =
        movieDAO.observeAllMovies().map { it.map(MovieEntity::toMovieDto) }

    override suspend fun deleteFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO.delete(movieDto.toEntity())
            }
        }
}
