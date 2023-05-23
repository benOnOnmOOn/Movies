package com.bz.movies.database.repository

import com.bz.dto.MovieDto
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.repository.mapper.toEntity
import com.bz.movies.database.repository.mapper.toMovieDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


internal class LocalMovieRepositoryImpl(
    private val movieDAO: MovieDAO
) : LocalMovieRepository {
    override suspend fun getFavoritesMovies(): Result<List<MovieDto>> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO
                    .getAllMovies()
                    .map { it.toMovieDto() }
            }
        }

    override suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO.insert(movieDto.toEntity())
            }
        }
}
