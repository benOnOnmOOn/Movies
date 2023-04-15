package com.bz.network.repository

import com.bz.network.api.model.MovieDetailsApiResponse
import com.bz.network.api.model.PlayingNowMoviesApiResponse
import com.bz.network.api.model.PopularMoviesPageApiResponse
import com.bz.network.api.service.MovieService
import com.bz.network.repository.mapper.toMovieDetailDto
import com.bz.network.repository.mapper.toPlayingNowMovieDto
import com.bz.network.repository.mapper.toPopularMovieDto
import com.bz.network.repository.mapper.toPopularMoviePageDto
import com.bz.network.repository.model.MoveDetailDto
import com.bz.network.repository.model.PlayingNowMovieDto
import com.bz.network.repository.model.PopularMovieDto
import com.bz.network.repository.model.PopularMoviePageDto
import com.bz.network.utils.InternetConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import retrofit2.Response

private const val AUTH_KEY = "55957fcf3ba81b137f8fc01ac5a31fb5"
private const val LANGUAGE = "en-US"

internal class MovieRepositoryImpl(
    private val movieService: MovieService,
    private val internetConnectionChecker: InternetConnection,
) : MovieRepository, KoinComponent {

    override suspend fun getPlayingNowMovies(): Result<List<PlayingNowMovieDto>> =
        executeApiCall(PlayingNowMoviesApiResponse::toPlayingNowMovieDto) {
            movieService.getNowPlayingMovies(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                page = "undefine"
            )
        }

    override suspend fun getPopularMovies(page: Int): Result<List<PopularMovieDto>> =
        executeApiCall(PopularMoviesPageApiResponse::toPopularMovieDto) {
            movieService.getPopularMoviePage(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                page = page
            )
        }

    override suspend fun getMovieDetail(movieId: Int): Result<MoveDetailDto> =
        executeApiCall(MovieDetailsApiResponse::toMovieDetailDto) {
            movieService.getMovieDetails(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                movieId = movieId,
            )
        }

    override suspend fun getPopularMoviesPage(page: Int): Result<PopularMoviePageDto> =
        executeApiCall(PopularMoviesPageApiResponse::toPopularMoviePageDto) {
            movieService.getPopularMoviePage(
                apiKey = AUTH_KEY,
                language = LANGUAGE,
                page = page
            )
        }

    private suspend inline fun <T, R> executeApiCall(
        crossinline mapper: (T) -> R,
        crossinline apiCall: suspend () -> Response<T>
    ): Result<R> {
        if (!internetConnectionChecker.isConnected)
            return Result.failure(NoInternetException())

        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()

                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        Result.success(mapper(apiResponse))
                    } ?: Result.failure(EmptyBodyException())
                } else {
                    Result.failure(HttpException(response.message()))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
