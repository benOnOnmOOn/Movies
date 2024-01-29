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
import com.bz.movies.database.repository.mapper.toPopularMovieEntity
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class LocalMovieRepositoryImpl(
    private val movieDAO: Lazy<MovieDAO>,
    private val playingNowMovieDAO: Lazy<PlayingNowMovieDAO>,
    private val popularMovieDAO: Lazy<PopularMovieDAO>
) : LocalMovieRepository {
    override val favoritesMovies: Flow<List<MovieDto>>
        get() =
            movieDAO
                .get()
                .observeAllMovies()
                .flowOn(Dispatchers.IO)
                .map { it.map(MovieEntity::toMovieDto) }

    override val playingNowMovies: Flow<List<MovieDto>>
        get() =
            playingNowMovieDAO
                .get()
                .observeAllMovies()
                .flowOn(Dispatchers.IO)
                .map { it.map(PlayingNowMovieEntity::toMovieDto) }

    override val popularMovies: Flow<List<MovieDto>>
        get() =
            popularMovieDAO
                .get()
                .observeAllMovies()
                .flowOn(Dispatchers.IO)
                .map { it.map(PopularMovieEntity::toMovieDto) }

    override suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO.get().insert(movieDto.toMovieEntity())
            }
        }

    override suspend fun deleteFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                movieDAO.get().delete(movieDto.toMovieEntity())
            }
        }

    override suspend fun insertPlayingNowMovies(movieDto: List<MovieDto>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                playingNowMovieDAO.get().insert(movieDto.map { it.toPlayingNowMovieEntity() })
            }
        }

    override suspend fun clearPlayingNowMovies(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            playingNowMovieDAO.get().clearTable()
        }
    }

    override suspend fun insertPopularMovies(movieDto: List<MovieDto>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                popularMovieDAO.get().insert(movieDto.map { it.toPopularMovieEntity() })
            }
        }

    override suspend fun clearPopularMovies(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            popularMovieDAO.get().clearTable()
        }
    }
}
