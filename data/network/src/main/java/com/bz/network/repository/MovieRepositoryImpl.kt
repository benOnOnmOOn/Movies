package com.bz.network.repository

import com.bz.dto.MovieDto
import com.bz.network.api.model.MovieDetailsApiResponse
import com.bz.network.api.model.PlayingNowMoviesApiResponse
import com.bz.network.api.model.PopularMoviesPageApiResponse
import com.bz.network.api.service.MovieService
import com.bz.network.repository.mapper.toMovieDetailDto
import com.bz.network.repository.mapper.toMovieDto
import com.bz.network.repository.mapper.toPopularMovieDto
import com.bz.network.repository.mapper.toPopularMoviePageDto
import com.bz.network.repository.model.MoveDetailDto
import com.bz.network.repository.model.PopularMoviePageDto
import com.bz.network.utils.InternetConnection
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.Response
import runSuspendCatching

private const val AUTH_KEY = "55957fcf3ba81b137f8fc01ac5a31fb5"
private const val LANGUAGE = "en-US"

internal class MovieRepositoryImpl(
    private val movieService: Lazy<MovieService>,
    private val internetConnectionChecker: Lazy<InternetConnection>
) : MovieRepository {
    override suspend fun getPlayingNowMovies(): Result<List<MovieDto>> =
        executeApiCall(PlayingNowMoviesApiResponse::toMovieDto) {
            movieService.get().getNowPlayingMovies(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                page = 1
            )
        }

    override suspend fun getPopularMovies(page: Int): Result<List<MovieDto>> =
        executeApiCall(PopularMoviesPageApiResponse::toPopularMovieDto) {
            movieService.get().getPopularMoviePage(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                page = page
            )
        }

    override suspend fun getMovieDetail(movieId: Int): Result<MoveDetailDto> =
        executeApiCall(MovieDetailsApiResponse::toMovieDetailDto) {
            movieService.get().getMovieDetails(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                movieId = movieId
            )
        }

    override suspend fun getPopularMoviesPage(page: Int): Result<PopularMoviePageDto> =
        executeApiCall(PopularMoviesPageApiResponse::toPopularMoviePageDto) {
            movieService.get().getPopularMoviePage(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                page = page
            )
        }

    private suspend inline fun <T, R> executeApiCall(
        crossinline mapper: (T) -> R,
        crossinline apiCall: suspend () -> Response<T>
    ): Result<R> = withContext(Dispatchers.IO) {
        if (!internetConnectionChecker.get().isConnected) {
            return@withContext Result.failure(NoInternetException())
        }

        runSuspendCatching {
            val response = apiCall()

            if (response.isSuccessful) {
                response
                    .body()
                    ?.let { apiResponse -> mapper(apiResponse) }
                    ?: throw EmptyBodyException()
            } else {
                throw HttpException(response.message())
            }
        }
    }
}
