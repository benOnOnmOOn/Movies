package com.bz.network.repository

import com.bz.dto.MoveDetailDto
import com.bz.dto.MovieDto
import com.bz.dto.PopularMoviePageDto
import com.bz.network.api.model.MovieDetailsApiResponse
import com.bz.network.api.model.PlayingNowMoviesApiResponse
import com.bz.network.api.model.PopularMoviesPageApiResponse
import com.bz.network.api.service.MovieService
import com.bz.network.repository.mapper.toMovieDetailDto
import com.bz.network.repository.mapper.toMovieDto
import com.bz.network.repository.mapper.toPopularMovieDto
import com.bz.network.repository.mapper.toPopularMoviePageDto
import com.bz.network.utils.InternetConnection
import dagger.Lazy

private const val AUTH_KEY = "55957fcf3ba81b137f8fc01ac5a31fb5"
private const val LANGUAGE = "en-US"

public interface MovieRepository {
    public suspend fun getPlayingNowMovies(): Result<List<MovieDto>>

    public suspend fun getPopularMovies(page: Int): Result<List<MovieDto>>

    public suspend fun getMovieDetail(movieId: Int): Result<MoveDetailDto>

    public suspend fun getPopularMoviesPage(page: Int): Result<PopularMoviePageDto>
}

internal class MovieRepositoryImpl(
    private val movieService: Lazy<MovieService>,
    private val internetConnectionChecker: Lazy<InternetConnection>
) : MovieRepository {
    override suspend fun getPlayingNowMovies(): Result<List<MovieDto>> = executeApiCall(
        internetConnectionChecker.get(),
        PlayingNowMoviesApiResponse::toMovieDto
    ) {
        movieService.get().getNowPlayingMovies(
            apiKey = AUTH_KEY,
            language = LANGUAGE,
            page = 1
        )
    }

    override suspend fun getPopularMovies(page: Int): Result<List<MovieDto>> = executeApiCall(
        internetConnectionChecker.get(),
        PopularMoviesPageApiResponse::toPopularMovieDto
    ) {
        movieService.get().getPopularMoviePage(
            apiKey = AUTH_KEY,
            language = LANGUAGE,
            page = page
        )
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MoveDetailDto> = executeApiCall(
        internetConnectionChecker.get(),
        MovieDetailsApiResponse::toMovieDetailDto
    ) {
        movieService.get().getMovieDetails(
            apiKey = AUTH_KEY,
            language = LANGUAGE,
            movieId = movieId
        )
    }

    override suspend fun getPopularMoviesPage(page: Int): Result<PopularMoviePageDto> =
        executeApiCall(
            internetConnectionChecker.get(),
            PopularMoviesPageApiResponse::toPopularMoviePageDto
        ) {
            movieService.get().getPopularMoviePage(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                page = page
            )
        }
}
