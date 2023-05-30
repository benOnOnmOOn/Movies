package com.bz.movies.database.repository

import com.bz.dto.MovieDto
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.dao.PlayingNowMovieDAO
import com.bz.movies.database.dao.PopularMovieDAO
import com.bz.movies.database.entity.MovieEntity
import com.bz.movies.database.entity.PlayingNowMovieEntity
import com.bz.movies.database.entity.PopularMovieEntity
import com.bz.movies.database.repository.mapper.toMovieDto
import com.bz.movies.database.repository.mapper.toMovieEntity
import com.bz.movies.database.repository.mapper.toPlayingNowMovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


internal class LocalMovieRepositoryImpl(
    private val movieDAO: MovieDAO,
    private val playingNowMovieDAO: PlayingNowMovieDAO,
    private val popularMovieDAO: PopularMovieDAO,
) : LocalMovieRepository {

    override val favoritesMovies: Flow<Result<List<MovieDto>>>
        get() = runCatching {
            movieDAO
                .observeAllMovies()
                .map { it.map(MovieEntity::toMovieDto) }
        }.fold(
            onSuccess = { it.map(Result.Companion::success) },
            onFailure = { flowOf(Result.failure(it)) }
        )
    override val playingNowMovies: Flow<Result<List<MovieDto>>>
        get() = runCatching {
            playingNowMovieDAO
                .observeAllMovies()
                .map { it.map(PlayingNowMovieEntity::toMovieDto) }
        }.fold(
            onSuccess = { it.map(Result.Companion::success) },
            onFailure = { flowOf(Result.failure(it)) }
        )
    override val popularMovies: Flow<Result<List<MovieDto>>>
        get() = runCatching {
            popularMovieDAO.observeAllMovies().map { it.map(PopularMovieEntity::toMovieDto) }
        }.fold(
            onSuccess = { it.map(Result.Companion::success) },
            onFailure = { flowOf(Result.failure(it)) }
        )

    override suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO.insert(movieDto.toMovieEntity())
            }
        }

    override suspend fun deleteFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO.delete(movieDto.toMovieEntity())
            }
        }

    override suspend fun insertPlayingNowMovies(movieDto: List<MovieDto>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                playingNowMovieDAO.insert(movieDto.map { it.toPlayingNowMovieEntity() })
            }
        }

    override suspend fun clearPlayingNowMovies(): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                playingNowMovieDAO.clearTable()
            }
        }

    override suspend fun insertPopularMovies(movieDto: List<MovieDto>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                playingNowMovieDAO.insert(movieDto.map { it.toPlayingNowMovieEntity() })
            }
        }

    override suspend fun clearPopularMovies(): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                popularMovieDAO.clearTable()
            }
        }
}
