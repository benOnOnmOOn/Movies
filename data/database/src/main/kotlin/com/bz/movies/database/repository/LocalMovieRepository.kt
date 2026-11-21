package com.bz.movies.database.repository

import com.bz.dto.MovieDto
import com.bz.movies.database.dao.CachedMovieDAO
import com.bz.movies.database.dao.MovieDAO
import com.bz.movies.database.entity.CachedMovieEntity
import com.bz.movies.database.entity.MovieCategory
import com.bz.movies.database.entity.MovieEntity
import com.bz.movies.database.repository.mapper.toCachedMovieEntity
import com.bz.movies.database.repository.mapper.toMovieDto
import com.bz.movies.database.repository.mapper.toMovieEntity
import com.bz.movies.database.runCatchingWithContext
import com.bz.tools.throwOnMainThread
import dagger.Lazy
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

public interface LocalMovieRepository {
    public val favoritesMovies: Flow<List<MovieDto>>

    public val playingNowMovies: Flow<List<MovieDto>>

    public val popularMovies: Flow<List<MovieDto>>

    public suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit>

    public suspend fun deleteFavoriteMovie(movieDto: MovieDto): Result<Unit>

    public suspend fun insertPlayingNowMovies(movieDto: List<MovieDto>): Result<Unit>

    public suspend fun clearPlayingNowMovies(): Result<Unit>

    public suspend fun insertPopularMovies(movieDto: List<MovieDto>): Result<Unit>

    public suspend fun clearPopularMovies(): Result<Unit>
}

internal class LocalMovieRepositoryImpl @Inject constructor(
    private val movieDAO: Lazy<MovieDAO>,
    private val cachedMovieDAO: Lazy<CachedMovieDAO>
) : LocalMovieRepository {
    init {
        throwOnMainThread()
    }

    override val favoritesMovies: Flow<List<MovieDto>>
        get() =
            movieDAO
                .get()
                .observeAllMovies()
                .flowOn(Dispatchers.IO)
                .map { it.map(MovieEntity::toMovieDto) }

    override val playingNowMovies: Flow<List<MovieDto>>
        get() =
            cachedMovieDAO
                .get()
                .observeMovies(MovieCategory.PLAYING_NOW)
                .flowOn(Dispatchers.IO)
                .map { it.map(CachedMovieEntity::toMovieDto) }

    override val popularMovies: Flow<List<MovieDto>>
        get() =
            cachedMovieDAO
                .get()
                .observeMovies(MovieCategory.POPULAR)
                .flowOn(Dispatchers.IO)
                .map { it.map(CachedMovieEntity::toMovieDto) }

    override suspend fun insertFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        runCatchingWithContext { movieDAO.get().insert(movieDto.toMovieEntity()) }

    override suspend fun deleteFavoriteMovie(movieDto: MovieDto): Result<Unit> =
        runCatchingWithContext { movieDAO.get().delete(movieDto.toMovieEntity()) }

    override suspend fun insertPlayingNowMovies(movieDto: List<MovieDto>): Result<Unit> =
        runCatchingWithContext {
            cachedMovieDAO.get().insert(
                movieDto.map { it.toCachedMovieEntity(MovieCategory.PLAYING_NOW) }
            )
        }

    override suspend fun clearPlayingNowMovies(): Result<Unit> =
        runCatchingWithContext { cachedMovieDAO.get().clear(MovieCategory.PLAYING_NOW) }

    override suspend fun insertPopularMovies(movieDto: List<MovieDto>): Result<Unit> =
        runCatchingWithContext {
            cachedMovieDAO.get().insert(
                movieDto.map { it.toCachedMovieEntity(MovieCategory.POPULAR) }
            )
        }

    override suspend fun clearPopularMovies(): Result<Unit> =
        runCatchingWithContext { cachedMovieDAO.get().clear(MovieCategory.POPULAR) }
}
