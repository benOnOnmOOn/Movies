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
import javax.inject.Inject

private const val AUTH_KEY = "55957fcf3ba81b137f8fc01ac5a31fb5"
private const val LANGUAGE = "en-US"

public interface MovieRepository {
    public suspend fun getPlayingNowMovies(): Result<List<MovieDto>>

    public suspend fun getPopularMovies(page: Int): Result<List<MovieDto>>

    public suspend fun getMovieDetail(movieId: Int): Result<MoveDetailDto>

    public suspend fun getPopularMoviesPage(page: Int): Result<PopularMoviePageDto>
}

internal class MovieRepositoryImpl @Inject constructor(
    private val movieService: MovieService,
    private val internetConnectionChecker: InternetConnection
) : MovieRepository {
    override suspend fun getPlayingNowMovies(): Result<List<MovieDto>> = executeApiCall(
        internetConnectionChecker,
        PlayingNowMoviesApiResponse::toMovieDto
    ) {
        movieService.getNowPlayingMovies(
            apiKey = AUTH_KEY,
            language = LANGUAGE,
            page = 1
        )
    }

    override suspend fun getPopularMovies(page: Int): Result<List<MovieDto>> = executeApiCall(
        internetConnectionChecker,
        PopularMoviesPageApiResponse::toPopularMovieDto
    ) {
        movieService.getPopularMoviePage(
            apiKey = AUTH_KEY,
            language = LANGUAGE,
            page = page
        )
    }

    override suspend fun getMovieDetail(movieId: Int): Result<MoveDetailDto> = executeApiCall(
        internetConnectionChecker,
        MovieDetailsApiResponse::toMovieDetailDto
    ) {
        movieService.getMovieDetails(
            apiKey = AUTH_KEY,
            language = LANGUAGE,
            movieId = movieId
        )
    }

    override suspend fun getPopularMoviesPage(page: Int): Result<PopularMoviePageDto> =
        executeApiCall(
            internetConnectionChecker,
            PopularMoviesPageApiResponse::toPopularMoviePageDto
        ) {
            movieService.getPopularMoviePage(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                page = page
            )
        }
}
